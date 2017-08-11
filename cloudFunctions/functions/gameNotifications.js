var functions = require('firebase-functions');
var sendNotifications = require("./sendNotifications.js"); 

// Loop through users in order with the forEach() method. The callback
// provided to forEach() will be called synchronously with a DataSnapshot
// for each child:
function sendGameNotifications() {  
  console.log("called send game notifications!");   
  var admin = require("firebase-admin");
  var db = admin.database();
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
            //console.log(email + " didn't join the game");
            return; 
          }
          var lastDonationStr = childSnapshot.val()["lastDonationInString"];
          var isInLast3Months = sendNotifications.checkLastDonationDate(lastDonationStr);
          if (isInLast3Months)
          {
            //console.log(email + " has donated in the last 3 months");
            return;
          }
          
          const payload = {
            notification: {
            title: "Your team needs you",
            body: "Donate blood, save a life, win the game!",
            icon: "blooddrop"
            }
          };          

          admin.messaging().sendToDevice(instanceId, payload)
            .then(function (response) {
              console.log("sent notification to " + email);
              //console.log("Successfully sent message:", response);
              })
            .catch(function (error) {
              console.log("Error sending message:", error);
            });
      });
    });
}

module.exports =sendGameNotifications;
