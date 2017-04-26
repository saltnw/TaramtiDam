var unirest = require("unirest")

const DOMAIN = "https://www.mdais.org";
const PAGE = "/index.php?option=com_blooddonations&view=blooddonations&Itemid=771&lang=he"

function obtainValidCookie(onValidCookie)
{
	unirest.get(DOMAIN).header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36").end(function(response) {
		extractValidCookie(response.body, (cookie) => onValidCookie(cookie));
	})
}

function extractValidCookie(response_body, onFinish)
{
	let stripped_response = response_body.replace(/<script>/g, "").replace(/<\/script>/g,"");
	stripped_response = stripped_response.match(/<body>(.*)<\/body>/)[1];

	stripped_response = stripped_response.replace("winsocks(true)", "window.winsocks(true)");
	stripped_response = stripped_response.replace("!f4()&&!!z4","true");
	stripped_response = stripped_response.replace("rbzns.challdomain=", "rbzns=window.rbzns; rbzns.challdomain=");

	stripped_response =  `window = {"document": {
                        "documentElement": {
                            "scrollLeft": ""
                         },
                   },
                   "screen": {
                         "width": 1920,
                         "height": 1080,
                         "availHeight": 1000,
                         "availWidth": 1000
                   },
                   "navigator": {
                          "userAgent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36"
                   },

				   "location":
				   {
					"reload" : () =>{}
				   }
                 }

document = window.document;
screen = window.screen;
navigator = window.navigator;
location = window.location; ` + stripped_response;

	eval(stripped_response);

	setTimeout(() => onFinish(document.cookie.match(/(rbzid=.*); e/)[1]), 50);
}

function LoadDonationPageInternal(cookie, onFinish)
{
	unirest.get(DOMAIN+PAGE).header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36").header("Cookie", cookie).end(function(response) { onFinish(response.body); });
}

// API - onFinish(page)
function LoadDonationPage(onFinish)
{
  obtainValidCookie((cookie) => { LoadDonationPageInternal(cookie, onFinish)});
}

module.exports = LoadDonationPage;
