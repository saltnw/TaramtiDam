var functions = require('firebase-functions');
var getMdaPage = require('./getMdaPage.js')
var processMDAPage = require('./processMDAPage.js');

exports.getMdaMobilesDB =
  functions.pubsub.topic('daily-tick').onPublish((event) => {
      return getMdaPage().then(function(page) { return processMDAPage(page); }).then(function() { console.log("Done Executing!")});
    })
