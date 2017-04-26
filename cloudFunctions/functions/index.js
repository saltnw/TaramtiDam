var functions = require('firebase-functions');
var getMdaPage = require('./getMdaPage.js')
var processMDAPage = require('./processMDAPage.js');

exports.daily_job =
  functions.pubsub.topic('daily-tick').onPublish((event) => {
    getMdaPage(processMDAPage)
  })
