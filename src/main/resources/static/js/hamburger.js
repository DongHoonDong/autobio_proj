function toggleMenu() {
    const nav = document.querySelector('nav');
    const authSection = document.querySelector('.auth-section');
    if (nav.classList.contains('active')) {
        nav.classList.remove('active');
        nav.style.right = '-100%';
        authSection.style.display = 'none';
    } else {
        nav.style.right = '0';
        nav.classList.add('active');
        authSection.style.display = 'block';
    }
}