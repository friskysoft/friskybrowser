package com.friskysoft.framework;

public class Utilities {

    public static final String JQUERY_LOADER_SCRIPT = "/** dynamically load jQuery */ "+
            "(function(jqueryUrl, callback) { "+
            "    if (typeof jqueryUrl != 'string') { "+
            "        jqueryUrl = 'https://code.jquery.com/jquery-%s.min.js'; "+
            "    } "+
            "    if (typeof jQuery == 'undefined') { "+
            "        var script = document.createElement('script'); "+
            "        var head = document.getElementsByTagName('head')[0]; "+
            "        var done = false; "+
            "        script.onload = script.onreadystatechange = (function() { "+
            "            if (!done && (!this.readyState || this.readyState == 'loaded' "+
            "                || this.readyState == 'complete')) { "+
            "                done = true; "+
            "                script.onload = script.onreadystatechange = null; "+
            "                head.removeChild(script); "+
            "                callback(); "+
            "            } "+
            "        }); "+
            "        script.src = jqueryUrl; "+
            "        head.appendChild(script); "+
            "    } "+
            "    else { "+
            "        callback(); "+
            "    } "+
            "})(arguments[0], arguments[arguments.length - 1]); ";
}
