var config = {
    apiKey: "AIzaSyDxjJHS6QcSn-30tB74V4yFoGAmaUpN6pA",
    authDomain: "clouds-and-droids.firebaseapp.com",
    databaseURL: "https://clouds-and-droids.firebaseio.com",
    storageBucket: "clouds-and-droids.appspot.com",
    messagingSenderId: "13403260399"
  };
firebase.initializeApp(config);

var Container = React.createClass({
    render: function() {
        return <div><div style={{float: "left", marginLeft: "100px"}}><Player start="0"/></div><div style={{float: "right", marginRight: "100px"}}><Player start="1"/></div></div>;
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
        if (hp == 0)
            image = "grumpy.png";
        return <div><p style={{textAlign: "center", fontSize: "18pt", color: "#3F51B5"}}><b>{name}</b></p><img src={image}/></div>;
    }
});

ReactDOM.render(
  <Container />,
  document.getElementById('root')
);