var unirest = require("unirest")
var moment = require("moment-timezone")

const DOMAIN = "https://b.mda.org.il"
const PAGE = "/Donation.aspx"

var headers = {
    "User-Agent":       "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36",
    "Content-Type":     'application/x-www-form-urlencoded'
}

//onValidCookie
function obtainValidCookie()
{
	return new Promise((resolve, reject) => {
		unirest.get(DOMAIN).header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36").end(function(response) {
		extractValidCookie(response, (cookie) => resolve(cookie));
		})
	})
}

function extractValidCookie(response, onFinish)
{
	onFinish("rbzid=" + response.cookies["rbzid"]);
}

function LoadDonationPageInternal(cookie)
{
	return new Promise((resolve, reject) => {
		unirest.get(DOMAIN+PAGE).header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36").header("Cookie", cookie).end(function(response) { resolve(response.body); });
	})
}

function LoadTommorowDonationPage(page)
{
    params = getParameters(page); 
	return new Promise((resolve, reject) => {
        unirest.post(DOMAIN+PAGE)
            .headers(headers)
            .send( params)
            .end(function (response) {
                resolve([page, response.body]);
        });
    })
}

function getParameters(page)
{
   var params = {}; 
   var cheerio = require('cheerio');
   $ = cheerio.load(page) 
   $("form#form2 :input").each(function(){
        var input = $(this);
        params[input.attr("name")] = input.attr("value");
    });

    var tommorow = moment().tz("Asia/Jerusalem").add("1", "days").format("YYYY-MM-DD");
    params["tbDate"] = tommorow;
    return params; 
}


// API - LoadDonationPage().then(...)
function LoadDonationPage()
{
	return LoadDonationPageInternal().then(LoadTommorowDonationPage);
}

//for testing
//LoadDonationPage().then((page) => console.log(page));
module.exports = LoadDonationPage;
