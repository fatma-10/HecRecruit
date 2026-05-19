(function () {
    'use strict';

    document.addEventListener('DOMContentLoaded', function () {
        document.body.classList.add('is-loaded');
        initStagger();
        initRevealOnScroll();
    });

    function initStagger() {
        document.querySelectorAll('[data-stagger]').forEach(function (container) {
            var children = container.children;
            for (var i = 0; i < children.length; i++) {
                children[i].style.animationDelay = (0.06 * i) + 's';
            }
        });
    }

    function initRevealOnScroll() {
        if (!('IntersectionObserver' in window)) return;

        var observer = new IntersectionObserver(function (entries) {
            entries.forEach(function (entry) {
                if (entry.isIntersecting) {
                    entry.target.classList.add('is-visible');
                    observer.unobserve(entry.target);
                }
            });
        }, { threshold: 0.12, rootMargin: '0px 0px -40px 0px' });

        document.querySelectorAll('.reveal-on-scroll').forEach(function (el) {
            observer.observe(el);
        });
    }
})();
