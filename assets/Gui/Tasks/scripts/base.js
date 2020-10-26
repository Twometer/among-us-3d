function $(s) {
    return document.getElementById(s);
}

Element.prototype.disableAni = function() {
    this.classList.remove("ani1");
    this.classList.remove("ani05");
}

var _baseX;
var _baseY;

function OnLoaded() {
    var background = document.getElementsByClassName("background")[0];
    var container = document.getElementsByClassName("container")[0];
    container.style.width = background.offsetWidth;
    container.style.height=background.offsetHeight;

    var abs_ = document.getElementsByClassName("abs");
    for (var a of abs_) {
        var t = a.getAttribute('t');
        var b = a.getAttribute('b');
        var l = a.getAttribute('l');
        var r = a.getAttribute('r');
        if (t != null) a.style.top = t;
        if (b != null) a.style.bottom = b;
        if (l != null) a.style.left = l;
        if (r != null) a.style.right = r;
    }
    document.body.style.zoom = "150%";
    _baseX = container.getBoundingClientRect().left;
    _baseY = container.getBoundingClientRect().top;
}

window.onresize = function() {
    var container = document.getElementsByClassName("container")[0];
    _baseX = container.getBoundingClientRect().left;
    _baseY = container.getBoundingClientRect().top;
}

OnLoaded();