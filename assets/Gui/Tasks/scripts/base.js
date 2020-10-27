function $(s) {
    return document.getElementById(s);
}

function _(s)  {
    return document.getElementsByClassName(s);
}

Element.prototype.disableAni = function() {
    this.classList.remove("ani1");
    this.classList.remove("ani05");
}

MouseEvent.prototype.getRealX = function() {
    return this.x / 1.5 - _baseX;
}

MouseEvent.prototype.getRealY = function() {
    return this.y / 1.5 - _baseY;
}

Element.prototype.hide = function() {
this.style.display='none';
}

Element.prototype.show = function() {
this.style.display = 'block';
}

var _baseX;
var _baseY;
var _baseW;
var _baseH;

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
    _baseW = container.getBoundingClientRect().right - container.getBoundingClientRect().left;
    _baseH = container.getBoundingClientRect().bottom - container.getBoundingClientRect().top;
    _baseX = container.getBoundingClientRect().left;
    _baseY = container.getBoundingClientRect().top;
}

window.onresize = function() {
    var container = document.getElementsByClassName("container")[0];
    _baseX = container.getBoundingClientRect().left;
    _baseY = container.getBoundingClientRect().top;
}



OnLoaded();

function shuffle(a) {
    for (let i = a.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [a[i], a[j]] = [a[j], a[i]];
    }
    return a;
}

function delay(d) {
    return new Promise((resolve, reject) => {
        setTimeout(function() {
            resolve();
        }, d);
    });
}