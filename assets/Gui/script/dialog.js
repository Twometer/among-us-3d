function showDialog(id) {
    document.getElementById(id).style.display = 'block';
}
function hideDialog(id) {
    document.getElementById(id).style.display = 'none';
}
var closeButtons = document.getElementsByClassName('close');
for (const btn of closeButtons) {
    btn.onclick = function() {
        var id = btn.getAttribute('dialog');
        hideDialog(id);
    }
}