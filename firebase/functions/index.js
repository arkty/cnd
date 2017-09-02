const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.effectFinished = functions.database.ref('/battles/{battleId}/states/{playerId}/effects/{effectIndex}').onWrite(event => {
    effect = event.data.current.val();
    if (effect != null && effect.duration === 0)
        return event.data.adminRef.root.child('battles').child(event.params.battleId).child('states')
                .child(event.params.playerId).child('effects').child(event.params.effectIndex).remove();
});

exports.newTurn = functions.database.ref('/battles/{battleId}/turns/{turnId}').onWrite(event => {
  // only process when a turn has been created.
  if (event.data.previous.val()) {
    return;
  }

  // читаем ходы
  admin.database().ref('/battles/' + event.params.battleId + '/turns/' + event.params.turnId).once('value')
	.then(function(snapshot) {
      // выясняем, кто кастит в этот ход
      var whoCasts = event.params.turnId % 2;
      // читаем игроков
      admin.database().ref('/battles/' + event.params.battleId + '/states').once('value')
      .then(function(snapshot) {
          var states = snapshot.val();
          var castingPlayer = states[whoCasts];
          var notCastingPlayer = states[(whoCasts + 1) % 2];
          var targetPlayer;
          if (whoCasts == event.data.current.val().target)
              targetPlayer = castingPlayer;
          else
              targetPlayer = notCastingPlayer;
          
          // сколько маны нужно отнять
          admin.database().ref('/cards/' + event.data.current.val().card).once('value')
          .then(function(snapshot) {
              var card = snapshot.val();
              // отнимаем
              castingPlayer.mana -= card.mana;
              
              // применяем эффекты с текущей карты
              var cardEffects = card.effects;
              if (!targetPlayer.effects)
              targetPlayer.effects = [];
              for (var i = 0; i < cardEffects.length; i++)
                  targetPlayer.effects.push(cardEffects[i]);
              
              // применяем все эффекты, которые уже есть на игроках
              playEffects(castingPlayer);
              playEffects(notCastingPlayer);
              
              // обновляем игроков
              return event.data.adminRef.root.child('battles').child(event.params.battleId)
                      .child('states').update(states);
          })
      })
  })
});
	
function playEffects(player) {
    if (!player.effects)
        return;
    for (var i = 0; i < player.effects.length; i++) {
        var effect = player.effects[i];
        if (effect && effect.duration > 0) {
            eval(effect.id + '(player)');
            effect.duration--;
        }
    }
}

function fire_damage(targetPlayer) {
    targetPlayer.hp -= getRandomInt(0, 11);
}

function frost_damage(targetPlayer) {
    targetPlayer.hp -= getRandomInt(0, 9);
}

function scare_damage(targetPlayer) {
    targetPlayer.hp -= getRandomInt(0, 5);
}

function acid_damage(targetPlayer) {
    targetPlayer.hp -= getRandomInt(0, 7);
}

function basic_heal(targetPlayer) {
    targetPlayer.hp += getRandomInt(0, 9) + 2;
}

function poison_damage(targetPlayer) {
    targetPlayer.hp -= getRandomInt(0, 7);
}

// Возвращает случайное целое число между min (включительно) и max (не включая max)
function getRandomInt(min, max) {
  return Math.floor(Math.random() * (max - min)) + min;
}
