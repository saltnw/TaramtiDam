var unirest = require("unirest")

//const DOMAIN = "https://www.mdais.org";
//const PAGE = "/index.php?option=com_blooddonations&view=blooddonations&Itemid=771&lang=he"
const DOMAIN = "https://b.mda.org.il"
const PAGE = "/Donation.aspx"


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

// API - LoadDonationPage().then(...)
function LoadDonationPage()
{
	/*return obtainValidCookie().then(function(cookie) {
		return LoadDonationPageInternal(cookie)
	})*/
	return LoadDonationPageInternal();
}
//for testing
//LoadDonationPage().then((page) => console.log(page));
module.exports = LoadDonationPage;
