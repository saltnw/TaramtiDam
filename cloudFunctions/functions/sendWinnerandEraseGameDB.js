var functions = require('firebase-functions');
var admin = require("firebase-admin");
var db = admin.database();

function sendWinnerandEraseGameDB()
{
    var gameRef = db.ref().child("Game");
    
    var winner = computeWinner(gameRef);
    console.log(winner);

    sendWinngingNotifications(winner); 

    eraseGameDB(gameRef);

}

function eraseGameDB(gameRef)
{
    gameRef.child("East").child("Donations").set(0);
    gameRef.child("West").child("Donations").set(0);
    gameRef.child("North").child("Donations").set(0);
    gameRef.child("South").child("Donations").set(0);
    
    gameRef.child("East").child("Day").child("Donations").set(0);
    gameRef.child("East").child("Night").child("Donations").set(0);

    gameRef.child("West").child("Day").child("Donations").set(0);
    gameRef.child("West").child("Night").child("Donations").set(0);

    gameRef.child("North").child("Day").child("Donations").set(0);
    gameRef.child("North").child("Night").child("Donations").set(0);

    gameRef.child("South").child("Day").child("Donations").set(0);
    gameRef.child("South").child("Night").child("Donations").set(0);

}

function sendWinngingNotifications(winner)
{
  var usersRef = db.ref().child("users");
    
    usersRef.once("value")
      .then(function(snapshot) {
        snapshot.forEach(function(childSnapshot) {
          var email = childSnapshot.val()["email"];
          var instanceId = childSnapshot.val()["instanceId"];
          if (instanceId == null)
          { 
            return; 
          }
          if (instanceId.trim().length == 0 )
          {
            return;
          }
         var alreadyJoinedTheGame = childSnapshot.val()["alreadyJoinedTheGame"];
         if (alreadyJoinedTheGame == null)
          {
            return; 
          }
          if (!alreadyJoinedTheGame)
          {
            console.log(email + " didn't join the game");
            return; 
          }
          const payload = {
            notification: {
            title: "TaramtiDam  - winners",
            body: winner,
            icon: "blooddrop"
            }
          };          

          admin.messaging().sendToDevice(instanceId, payload)
            .then(function (response) {
              console.log("sent notification to " + email);
              })
            .catch(function (error) {
              console.log("Error sending message:", error);
            });
      });
    });
}

function computeWinner(gameRef)
{
    var dayDonations = computerPerVampTeam(gameRef, "Day");
    var nightDonations = computerPerVampTeam(gameRef, "Night");

    if (dayDonations > nightDonations)
    {
        return "The winners are the day vampiers! Bravo!"; 
    }
    else 
    {
        return "The winners are the night vampiers! Bravo!"; 
    }
}

function computerPerVampTeam(gameRef, vampTeam)
{ 
    var donations = 0;

    var eastRef = gameRef.child("East").child(vampTeam);
    var eastDonations = computePerSpecificTeam(eastRef); 

    var westRef = gameRef.child("West").child(vampTeam);
    var westDonations = computePerSpecificTeam(westRef); 

    var northRef = gameRef.child("North").child(vampTeam);
    var northDonations = computePerSpecificTeam(northRef); 

    var southRef = gameRef.child("South").child(vampTeam);
    var southDonations = computePerSpecificTeam(southRef); 

    donations = eastDonations + westDonations + southDonations + northDonations; 

    return donations; 
}

function computePerSpecificTeam(teamRef)
{
    var donations = 0; 
    teamRef.once("value").then(function(snapshot) {
            snapshot.forEach(function(childSnapshot) {
            donations = childSnapshot.val()["donations"];
        });
      });
      return donations; 
}

module.exports = sendWinnerandEraseGameDB;
