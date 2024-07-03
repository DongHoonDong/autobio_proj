    document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.content-preview').forEach(function(element) {
        let text = element.innerText;
        if (text.length > 300) {
            element.innerText = text.substring(0, 300) + '...';
        }
    });
});