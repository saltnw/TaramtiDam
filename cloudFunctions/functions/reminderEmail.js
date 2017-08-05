var functions = require('firebase-functions');
var admin = require("firebase-admin");
var db = admin.database();

const nodemailer = require('nodemailer');
var sendNotifications = require("./sendNotifications.js"); 

var transporter = nodemailer.createTransport({
    service: 'gmail',
    auth: {
        user: 'saltnworkshop@gmail.com',
        pass: 'a1a2a3a4'
    }
});

function reminderEmail()
{
    var usersRef = db.ref().child("users");
    usersRef.once("value")
      .then(function(snapshot) {
        snapshot.forEach(function(childSnapshot) {
          var uid = childSnapshot.val()["uid"];
          var email = childSnapshot.val()["email"];
          if (email.trim().length == 0 )
          {
            return;
          }
          var alreadySentReminder = childSnapshot.val()["alreadySentReminder"];
          if (alreadySentReminder != null && alreadySentReminder != undefined)
          {
            if (alreadySentReminder == true)
            {
              //console.log("already sent reminder to " + email);
              return;
            }
          }
          var lastDonationStr = childSnapshot.val()["lastDonationInString"];
          if (lastDonationStr.trim().length == 0)
          {
              //this person has not donated at all
              //console.log(email + " has never donated");
              return;
          }
          var isInLast3Months = sendNotifications.checkLastDonationDate(lastDonationStr);
          if (isInLast3Months)
          {
            //console.log("it has not been 3 months since " + email +  " has donated");
            return;
          }
          else
          {
              sendEmail(email);
              console.log("sent donation reminder email to " + email);
              usersRef.child(uid).child("alreadySentReminder").set(true);
          }

      });
    });
}


function sendEmail(email)
{
  var appLink = '<p DIR="RTL">' + "https://play.google.com/store/apps/details?id=com.taramtidam.taramtidam" + '</p>'
  // setup email data with unicode symbols
  var msgStr = "שלום" + ", " + "<br>" + "עברו 3 חודשים מתרומת הדם האחרונה שלך! עכשיו אפשר לחזור ולתרום!"
  var htmlStr = '<p DIR="RTL"> <b>' + msgStr + '</b></p>' + appLink
  let mailOptions = 
  {
      from: '"TaramtiDam" <TaramtiDam@gmail.com>', // sender address
      to: email, // list of receivers
      subject: "תזכורת מ-TaramtiDam", // Subject line
      html: htmlStr // html body
  };
  transporter.sendMail(mailOptions, (error, info) => {
    if (error) {
        return console.log(error);
    }
    console.log('Message %s sent: %s', info.messageId, info.response);
});
}

module.exports = reminderEmail;
