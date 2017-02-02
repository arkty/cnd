var config = {
    apiKey: "AIzaSyDxjJHS6QcSn-30tB74V4yFoGAmaUpN6pA",
    authDomain: "clouds-and-droids.firebaseapp.com",
    databaseURL: "https://clouds-and-droids.firebaseio.com",
    storageBucket: "clouds-and-droids.appspot.com",
    messagingSenderId: "13403260399"
  };
firebase.initializeApp(config);

var Spell = React.createClass({
    mixins: [ReactFireMixin],

    getInitialState: function() {
    return {
        turn: {},
        cards: []
      };
    },

    componentWillMount: function() {
        var turnRef = firebase.database().ref("battles/35:74:d6:65/turns/" + this.props.lastturn);
        this.bindAsObject(turnRef, "turn");
        var cardsRef = firebase.database().ref("cards");
        this.bindAsArray(cardsRef, "cards");
    },

    render: function() {
        if (this.state.turn.target == this.props.lastturn % 2)
            return false;
        var spellname;
        for (var i = 0; i < this.state.cards.length; i++) {
            if (this.state.cards[i][".key"].localeCompare(this.state.turn.card) == 0)
                spellname = this.state.cards[i]["name"];
        }
	if (this.state.turn.target == 0)
            spellname = "<- " + spellname;
	if (this.state.turn.target == 1)
            spellname = spellname + " ->";
        return <div><b style={{textAlign: "center", fontSize: "20pt", color: "#FF4081"}}>{spellname}</b></div>;
    }
});

var SelfSpell = React.createClass({
    mixins: [ReactFireMixin],

    getInitialState: function() {
    return {
        turn: {},
        cards: []
      };
    },

    componentWillMount: function() {
        var turnRef = firebase.database().ref("battles/35:74:d6:65/turns/" + this.props.lastturn);
        this.bindAsObject(turnRef, "turn");
        var cardsRef = firebase.database().ref("cards");
        this.bindAsArray(cardsRef, "cards");
    },

    render: function() {
        if (this.state.turn.target != this.props.lastturn % 2 || this.state.turn.target != this.props.id)
            return false;
        var spellname;
        for (var i = 0; i < this.state.cards.length; i++) {
            if (this.state.cards[i][".key"].localeCompare(this.state.turn.card) == 0)
                spellname = this.state.cards[i]["name"];
        }
        return <div><b style={{textAlign: "center", fontSize: "20pt", color: "#FF4081"}}>{spellname}</b></div>;
    }
});

var Container = React.createClass({

    mixins: [ReactFireMixin],

    getInitialState: function() {
    return {
        lastturn: 0
      };
    },

    componentWillMount: function() {
        var turnRef = firebase.database().ref("battles/35:74:d6:65/last_turn");
        this.bindAsObject(turnRef, "lastturn");
    },

    render: function() {
        if (this.state.lastturn[".value"] == undefined)
            return <div>
		<div style={{float: "left", marginLeft: "100px"}}><Player start="0"/></div>
		<div style={{float: "right", marginRight: "100px"}}><Player start="1"/></div>
	       </div>;
        return <div  style={{textAlign: "center"}}>
		<div style={{display: "inline-block", width: "25%", verticalAlign: "top"}}><Player start="0" lastturn={this.state.lastturn[".value"]}/></div>
		<div style={{display: "inline-block", width: "50%", verticalAlign: "middle"}}><Spell lastturn={this.state.lastturn[".value"]}/></div>
		<div style={{display: "inline-block", width: "25%", verticalAlign: "top"}}><Player start="1" lastturn={this.state.lastturn[".value"]}/></div>
	       </div>;
    }
});

var Player = React.createClass({

    mixins: [ReactFireMixin],

    getInitialState: function() {
    return {
        player: {},
        settings: {}
      };
    },

    componentWillMount: function() {
        var playerRef = firebase.database().ref("battles/35:74:d6:65/states/" + this.props.start);
        this.bindAsObject(playerRef, "player");
        var settingsRef = firebase.database().ref("settings");
        this.bindAsObject(settingsRef, "settings");
    },

    render: function() {
        //var name = this.state.player[".key"];
        var name = this.state.player["name"];
        var hp = this.state.player.hp;
        var maxHp = this.state.settings.max_hp;
        var image = "smile.png";
        if (hp < (maxHp / 3.0) * 2.0)
            image = "neutral.png";
        if (hp < maxHp / 3.0)
            image = "sad.png";
        if (hp <= 0)
            image = "grumpy.png";
        return <div>
		<p style={{textAlign: "center", fontSize: "18pt", color: "#3F51B5"}}><b>{name}</b></p>
		<img src={image}/>
		<SelfSpell lastturn={this.props.lastturn} id={this.props.start}/>
	       </div>;
    }
});

ReactDOM.render(
  <Container />,
  document.getElementById('root')
);