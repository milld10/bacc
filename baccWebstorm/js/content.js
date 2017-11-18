
var firstHref = $("a[href^='http']").eq(0).attr("href");

console.log(firstHref);

chrome.runtime.onMessage.addListener(
    function(request, sender, sendResponse) {
        if( request.message === "clicked_browser_action" ) {
            var firstHref = $("a[href^='http']").eq(0).attr("href");

            console.log(firstHref);
        }
    }
);

chrome.runtime.sendMessage({greeting: "hi"}, function (response) {
    console.log(response.farewell);
});


chrome.runtime.onMessage.addListener(
    function(request, sender, sendResponse) {
        console.log(sender.tab ?
            "from a content script:" + sender.tab.url :
            "from the extension");
        if (request.greeting == "hi")
            sendResponse({farewell: "goodbye"});
    });