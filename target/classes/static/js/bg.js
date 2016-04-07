/**
 * Created by zhang on 2016/2/16.
 */
particlesJS("particles-js", {
    particles: {
        color: '#F599BE',
        shape: "circle",
        opacity: 1,
        size: 1,
        size_random: !0,
        nb: 200,
        line_linked: {
            enable_auto: !0,
            distance: 100,
            color: "#46BCF3",
            opacity: .4,
            width: 1,
            condensed_mode: {enable: !1, rotateX: 600, rotateY: 600}
        },
        anim: {enable: !0, speed: 2.5}
    },
    interactivity: {
        enable: !0,
        mouse: {distance: 250},
        detect_on: "canvas",
        mode: "grab",
        line_linked: {opacity: .35},
        events: {onclick: {enable: !0, mode: "push", nb: 3}}
    },
    retina_detect: !0
});

