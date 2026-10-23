const revealObserver = new IntersectionObserver((entries) => {
  entries.forEach((entry) => {
    if (entry.isIntersecting) {
      entry.target.classList.add('is-visible');
      revealObserver.unobserve(entry.target);
    }
  });
}, { threshold: 0.12 });

document.querySelectorAll('.reveal').forEach((element) => revealObserver.observe(element));

const card = document.querySelector('#device-card');
const visual = document.querySelector('.hero-visual');
if (card && visual && window.matchMedia('(pointer: fine)').matches) {
  visual.addEventListener('pointermove', (event) => {
    const bounds = visual.getBoundingClientRect();
    const x = (event.clientX - bounds.left) / bounds.width - 0.5;
    const y = (event.clientY - bounds.top) / bounds.height - 0.5;
    card.style.transform = `translate(-50%, -50%) rotateY(${x * -28 - 21}deg) rotateX(${y * -18 + 8}deg) rotateZ(${x * 7 + 7}deg)`;
  });
  visual.addEventListener('pointerleave', () => {
    card.style.transform = 'translate(-50%,-50%) rotateY(-21deg) rotateX(8deg) rotateZ(7deg)';
  });
}

const cursorGlow = document.querySelector('.cursor-glow');
if (cursorGlow && window.matchMedia('(pointer: fine)').matches) {
  window.addEventListener('pointermove', (event) => {
    cursorGlow.style.transform = `translate(${event.clientX - 160}px, ${event.clientY - 160}px)`;
  });
}
