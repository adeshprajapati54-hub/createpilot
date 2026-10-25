// CreatorPilot AI — Web Studio & Landing Controller

document.addEventListener('DOMContentLoaded', () => {
  initMobileMenu();
  initTabs();
  initIdeaLab();
  initScriptStudio();
  initScenes();
  initSeoAnalyzer();
  initPricingToggle();
  initCheckoutModal();
  initDownloadTrack();
});

// Mobile Navigation
function initMobileMenu() {
  const toggle = document.getElementById('mobileToggle');
  const menu = document.getElementById('navMenu');
  if (!toggle || !menu) return;

  toggle.addEventListener('click', () => {
    menu.classList.toggle('mobile-open');
  });

  menu.querySelectorAll('a').forEach(link => {
    link.addEventListener('click', () => {
      menu.classList.remove('mobile-open');
    });
  });
}

// Studio Tabs
function initTabs() {
  const tabs = document.querySelectorAll('.tab-btn');
  const panes = document.querySelectorAll('.tab-pane');

  tabs.forEach(tab => {
    tab.addEventListener('click', () => {
      tabs.forEach(t => t.classList.remove('active'));
      panes.forEach(p => p.classList.remove('active'));

      tab.classList.add('active');
      const targetPane = document.getElementById(`pane-${tab.dataset.tab}`);
      if (targetPane) targetPane.classList.add('active');
    });
  });
}

// Tab 1: Idea Lab
const sampleIdeas = {
  tech: [
    { title: "I Threw Away My Triple Monitors for Smart Glasses", score: 96, angle: "Radical Setup Swap" },
    { title: "The $49 AI Hardware That Puts Apple to Shame", score: 92, angle: "Budget Disrupter" },
    { title: "Why Every Programmer is Ditching MacBooks in 2026", score: 89, angle: "Controversial Shift" }
  ],
  productivity: [
    { title: "The 3-Hour Deep Work Block That Replaced My 8-Hour Job", score: 95, angle: "Workplace Efficiency" },
    { title: "Why Notion is Actually Ruining Your Productivity", score: 91, angle: "Common Trap Exposure" },
    { title: "I Built an Automated YouTube Studio in a 6x6 Closet", score: 94, angle: "Extreme Minimalist Space" }
  ],
  finance: [
    { title: "The 2026 Wealth Shift Nobody Is Talking About", score: 97, angle: "Macro Urgency" },
    { title: "How 20-Somethings are Earning $12k/mo with Micro-SaaS", score: 93, angle: "Actionable Blueprint" },
    { title: "Why Index Funds Might Not Be Enough Any More", score: 88, angle: "Contrarian Advice" }
  ],
  gaming: [
    { title: "The Silent Death of Open-World Games", score: 95, angle: "Gaming Industry Essay" },
    { title: "Why Unreal Engine 6 Will Break the Internet", score: 93, angle: "Tech Speculation" },
    { title: "100 Players Survived 30 Days in Hardcore VR", score: 98, angle: "Challenge Narrative" }
  ]
};

function renderIdeas(category) {
  const container = document.getElementById('ideasList');
  if (!container) return;
  const list = sampleIdeas[category] || sampleIdeas.tech;

  container.innerHTML = list.map(item => `
    <div class="idea-card-item">
      <div class="idea-meta-row">
        <span class="idea-badge">${item.angle}</span>
        <span class="idea-score">${item.score}/100 CTR</span>
      </div>
      <div class="idea-title-text">${item.title}</div>
    </div>
  `).join('');
}

function initIdeaLab() {
  const nicheSelect = document.getElementById('nicheSelect');
  const genBtn = document.getElementById('genIdeasBtn');

  renderIdeas(nicheSelect ? nicheSelect.value : 'tech');

  if (genBtn && nicheSelect) {
    genBtn.addEventListener('click', () => {
      genBtn.disabled = true;
      genBtn.innerHTML = '<span>⚡ Synthesizing with Gemini...</span>';
      setTimeout(() => {
        renderIdeas(nicheSelect.value);
        genBtn.disabled = false;
        genBtn.innerHTML = '<span>⚡ Generate Viral Angles</span>';
        showToast('Fresh viral hooks generated for your niche!');
      }, 500);
    });

    nicheSelect.addEventListener('change', () => {
      renderIdeas(nicheSelect.value);
    });
  }
}

// Tab 2: Script Studio
let selectedTone = 'Fast-Paced';

const sampleScripts = {
  'Fast-Paced': [
    { section: "00:00 - HOOK (Retention Lock)", text: "Look at this empty desk. No monitors, no docking stations, no 50 cables tangled behind my chair. For the last 30 days, I replaced my entire $4,500 workstation with a pair of lightweight AR glasses... and honestly, my workflow will never be the same." },
    { section: "00:35 - CONFLICT & THE REALITY", text: "Here's what the marketing videos never tell you: day 3 gave me eye fatigue, and typing on a virtual 120-inch curved display while drinking coffee felt bizarre. But by day 12, something clicked." },
    { section: "02:15 - THE FINAL VERDICT & CTA", text: "If you travel or live in a tiny studio apartment, this is genuinely the future. If you edit 8K video for 10 hours a day, wait for Gen 2. Drop a comment below with your current screen setup—and subscribe for the full battery test next week!" }
  ],
  'Storyteller': [
    { section: "ACT 1: THE OBSESSION", text: "Three months ago, my desk was an ergonomic nightmare. Three 32-inch monitors, two arm mounts, and a room that felt more like NASA control than a creative studio. I wanted freedom." },
    { section: "ACT 2: THE TRIAL BY FIRE", text: "I packed just a laptop and a headset into a backpack and headed to a coffee shop in Kyoto. For the first time in five years, I had a private quad-monitor rig on a tiny round table." },
    { section: "ACT 3: THE TURNING POINT", text: "Technology is at its best when it disappears. Today, I'm sharing the exact setup and trade-offs you must know before selling your displays." }
  ],
  'Analytical': [
    { section: "BENCHMARK 1: RESOLUTION & PPD", text: "Let's examine the optics. At 49 Pixels Per Degree, text rendering matches a 1440p monitor viewed at 24 inches. Here is the micro-OLED panel comparison." },
    { section: "BENCHMARK 2: LATENCY & REFRESH", text: "Testing at 120Hz display refresh over direct DisplayPort Alt Mode revealed a 12ms motion-to-photon latency, virtually eliminating motion sickness." },
    { section: "RECOMMENDATION MATRIX", text: "Based on our 30-day telemetry, developer productivity rose by 14% while desk footprint dropped to zero. Here is who should buy today." }
  ],
  'Casual': [
    { section: "INTRO CHAT", text: "Alright friends, so I did something kind of crazy this month. I packed all my monitors into storage and promised myself I wouldn't touch a physical screen for 30 days." },
    { section: "THE EXPERIENCE", text: "The first week was hilarious—I looked like a robot tapping invisible air at the kitchen counter. But watch how fast you can snap windows in 3D space." },
    { section: "WRAP UP", text: "Let me know in the comments if you'd ever dare to do this! See you in the next one." }
  ]
};

function renderScript(tone) {
  const container = document.getElementById('scriptReader');
  if (!container) return;
  const sections = sampleScripts[tone] || sampleScripts['Fast-Paced'];

  container.innerHTML = sections.map(sec => `
    <div class="script-section-block">
      <span class="script-tag">${sec.section}</span>
      <p class="script-text">${sec.text}</p>
    </div>
  `).join('');
}

function initScriptStudio() {
  const chips = document.querySelectorAll('#toneChips .chip');
  const genBtn = document.getElementById('genScriptBtn');
  const copyBtn = document.getElementById('copyScriptBtn');

  renderScript(selectedTone);

  chips.forEach(chip => {
    chip.addEventListener('click', () => {
      chips.forEach(c => c.classList.remove('active'));
      chip.classList.add('active');
      selectedTone = chip.dataset.tone;
      renderScript(selectedTone);
    });
  });

  if (genBtn) {
    genBtn.addEventListener('click', () => {
      genBtn.disabled = true;
      genBtn.innerHTML = '<span>⚡ Drafting Outline...</span>';
      setTimeout(() => {
        renderScript(selectedTone);
        genBtn.disabled = false;
        genBtn.innerHTML = '<span>⚡ Draft 3-Part Script</span>';
        showToast(`Script updated with ${selectedTone} cadence!`);
      }, 500);
    });
  }

  if (copyBtn) {
    copyBtn.addEventListener('click', () => {
      const container = document.getElementById('scriptReader');
      if (container) {
        navigator.clipboard.writeText(container.innerText).then(() => {
          showToast('Script copied to clipboard!');
        });
      }
    });
  }
}

// Tab 3: Scene Studio
const scenesData = [
  { num: "SCENE 01", title: "Cold Open A-Roll", desc: "Medium close-up at desk. Creator gestures to completely empty desk surface.", angle: "Eye Level 50mm", time: "0:00 - 0:15" },
  { num: "SCENE 02", title: "First-Person B-Roll", desc: "POV shot wearing glasses. Virtual ultra-wide browser window floats in air.", angle: "POV 16mm Lens", time: "0:15 - 0:45" },
  { num: "SCENE 03", title: "Side-by-Side Comparison", desc: "Split screen: Traditional bulky triple monitors vs single slim carry case.", angle: "Macro / Detail", time: "0:45 - 1:20" },
  { num: "SCENE 04", title: "Conclusion & Call to Action", desc: "Wide room shot showing creator relaxed on couch typing with bluetooth keyboard.", angle: "Slow Push-in 35mm", time: "1:20 - 2:00" }
];

function initScenes() {
  const container = document.getElementById('scenesGrid');
  if (!container) return;

  container.innerHTML = scenesData.map(scene => `
    <div class="scene-card-item">
      <span class="scene-num">${scene.num}</span>
      <h4 class="scene-title">${scene.title}</h4>
      <p class="scene-desc">${scene.desc}</p>
      <div class="scene-meta">
        <span>🎥 ${scene.angle}</span>
        <span>⏱️ ${scene.time}</span>
      </div>
    </div>
  `).join('');
}

// Tab 4: SEO Analyzer
function initSeoAnalyzer() {
  const btn = document.getElementById('analyzeSeoBtn');
  const input = document.getElementById('seoInputTitle');
  const scoreVal = document.getElementById('seoScoreVal');

  if (btn && input && scoreVal) {
    btn.addEventListener('click', () => {
      const title = input.value.trim();
      if (!title) return;

      // Realistic CTR calculation based on length, emotional words, numbers
      let score = 75;
      if (/[0-9]/.test(title)) score += 8;
      if (/[!?]/.test(title)) score += 6;
      if (/\b(why|how|stop|best|never|secret|truth|insane)\b/i.test(title)) score += 7;
      if (title.length >= 35 && title.length <= 65) score += 4;
      score = Math.min(score, 99);

      scoreVal.innerText = score;
      showToast(`Analyzed! Predicted CTR Score: ${score}/100`);
    });
  }
}

// Pricing Toggle (Monthly vs Annual with 20% discount)
function initPricingToggle() {
  const toggle = document.getElementById('billingToggle');
  const monthlyLabel = document.getElementById('monthlyLabel');
  const annualLabel = document.getElementById('annualLabel');
  const priceElements = document.querySelectorAll('.price-val');
  const annualNotes = document.querySelectorAll('.annual-note');

  if (!toggle) return;

  let isAnnual = false;

  toggle.addEventListener('click', () => {
    isAnnual = !isAnnual;
    toggle.classList.toggle('annual', isAnnual);
    monthlyLabel.classList.toggle('active', !isAnnual);
    annualLabel.classList.toggle('active', isAnnual);

    priceElements.forEach(el => {
      const monthly = el.dataset.monthly;
      const annual = el.dataset.annual;
      el.innerText = isAnnual ? annual : monthly;
    });

    annualNotes.forEach(note => {
      note.classList.toggle('hidden', !isAnnual);
    });
  });
}

// Checkout Modal
function initCheckoutModal() {
  const modal = document.getElementById('checkoutModal');
  const closeBtn = document.getElementById('modalCloseBtn');
  const planButtons = document.querySelectorAll('.select-plan-btn');
  const confirmBtn = document.getElementById('confirmPaymentBtn');

  const modalPlanSelected = document.getElementById('modalPlanSelected');
  const modalCadenceSelected = document.getElementById('modalCadenceSelected');
  const modalTotalDue = document.getElementById('modalTotalDue');

  if (!modal) return;

  planButtons.forEach(btn => {
    btn.addEventListener('click', () => {
      const planName = btn.dataset.planName || 'Pro Plan';
      const isAnnual = document.getElementById('billingToggle')?.classList.contains('annual');
      const card = btn.closest('.pricing-card');
      const priceVal = card?.querySelector('.price-val')?.innerText || '49';

      modalPlanSelected.innerText = planName;
      modalCadenceSelected.innerText = isAnnual ? 'Annual (20% Savings)' : 'Monthly';
      modalTotalDue.innerText = isAnnual ? `$${parseInt(priceVal, 10) * 12}.00 / year` : `$${priceVal}.00 / month`;

      modal.classList.add('open');
    });
  });

  const closeModal = () => modal.classList.remove('open');
  if (closeBtn) closeBtn.addEventListener('click', closeModal);
  modal.addEventListener('click', (e) => {
    if (e.target === modal) closeModal();
  });

  if (confirmBtn) {
    confirmBtn.addEventListener('click', () => {
      confirmBtn.disabled = true;
      confirmBtn.innerText = 'Verifying HMAC-SHA256 signature...';

      setTimeout(() => {
        confirmBtn.disabled = false;
        confirmBtn.innerText = 'Confirm & Activate Subscription';
        closeModal();
        showToast('🎉 Subscription activated! Welcome to CreatorPilot.');
      }, 1000);
    });
  }
}

// Download Toast
function initDownloadTrack() {
  const downloadLinks = document.querySelectorAll('a[download]');
  downloadLinks.forEach(link => {
    link.addEventListener('click', () => {
      showToast('⬇️ Downloading CreatorPilot.apk (23MB)...');
    });
  });
}

// Toast helper
function showToast(message) {
  const toast = document.getElementById('toastMessage');
  if (!toast) return;
  toast.innerText = message;
  toast.classList.add('show');
  setTimeout(() => {
    toast.classList.remove('show');
  }, 3500);
}
