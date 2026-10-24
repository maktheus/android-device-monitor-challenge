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

function downloadBrowserCSV() {
  const csv = "ID,Timestamp,Data_Hora,Eixo_X,Eixo_Y,Eixo_Z,Magnitude,Dispositivo\n" +
    '1,1792794076000,"23/10/2026 18:21:16",-2.48,4.47,9.67,10.93,"Samsung Galaxy S23 Ultra"\n' +
    '2,1792794087000,"23/10/2026 18:21:27",-0.16,2.98,9.36,9.82,"Samsung Galaxy S23 Ultra"\n' +
    '3,1792794097000,"23/10/2026 18:21:37",-0.87,3.29,9.01,9.63,"Samsung Galaxy S23 Ultra"\n' +
    '4,1792799651681,"23/10/2026 19:54:11",0.00,9.78,0.81,9.81,"Emulador Pixel Android 14"\n';
  const blob = new Blob([csv], { type: "text/csv;charset=utf-8;" });
  const link = document.createElement("a");
  link.href = URL.createObjectURL(blob);
  link.download = "leituras_acelerometro_adm.csv";
  link.click();
}

