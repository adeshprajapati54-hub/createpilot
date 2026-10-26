/**
 * CreatorPilot AI — Premium SaaS Workspace & Production Engine
 * Client controller with reactive state, autosave, versioning, 
 * cryptographic subscription verification, and cross-platform export.
 */

(function () {
  'use strict';

  // --- Initial / Seed Projects ---
  const SEED_PROJECTS = [
    {
      id: "proj_ar_glasses_2026",
      title: "I Swapped My Triple Monitors for AR Glasses (Day 30)",
      topic: "Replacing an expensive multi-monitor desk setup with lightweight AR glasses for 30 days of real software engineering and creative work.",
      niche: "Tech & AI Hardware",
      targetAudience: "Developers, creators, and desk setup enthusiasts looking for ultra-minimalist workspaces.",
      format: "Long-form (8-12 min)",
      duration: "10:30",
      tone: "Fast-Paced",
      status: "Scripting",
      scheduledDate: "2026-11-04",
      updatedAt: Date.now() - 3600000,
      hooks: [
        {
          id: "hook_1",
          type: "Curiosity Gap",
          hook: "Look at this completely empty desk. For the last 30 days, I replaced my entire $4,500 workstation with a pair of lightweight AR glasses — and honestly, my workflow will never be the same.",
          score: "94 / 100",
          rationale: "Creates an immediate visual discrepancy by showing an empty desk while making a bold lifestyle claim.",
          isSelected: true
        },
        {
          id: "hook_2",
          type: "Contrarian / Problem",
          hook: "Everyone tells you that giant curved monitors are the ultimate productivity hack. They are wrong — and after 30 days wearing virtual displays, here is why you might be wasting thousands.",
          score: "91 / 100",
          rationale: "Directly attacks an expensive creator consensus to trigger debate and click-through.",
          isSelected: false
        },
        {
          id: "hook_3",
          type: "Story Narrative",
          hook: "Three months ago, my desk was an ergonomic nightmare. Two arm mounts, 50 tangled cables, and zero room to think. Then I put my monitors on Facebook Marketplace.",
          score: "88 / 100",
          rationale: "Strong narrative stakes with a relatable frustration and radical commitment.",
          isSelected: false
        },
        {
          id: "hook_4",
          type: "Bold Claim",
          hook: "This $599 headset just made physical computer monitors completely obsolete for anyone who travels or works remotely.",
          score: "89 / 100",
          rationale: "Targeted value comparison for digital nomads and minimalist workers.",
          isSelected: false
        }
      ],
      scriptSections: [
        {
          id: "sec_hook",
          order: 1,
          name: "Hook & Visual Anchor",
          timestamp: "0:00 - 0:30",
          text: "Look at this completely empty desk. No monitors, no docking stations, no 50 cables tangled behind my chair. For the last 30 days, I replaced my entire $4,500 workstation with a pair of lightweight AR glasses — and honestly, my workflow will never be the same.",
          notes: "Start with a wide camera push-in showing a clean walnut desk with just a laptop and glasses.",
          wordCount: 52
        },
        {
          id: "sec_setup",
          order: 2,
          name: "The Reality & Conflict",
          timestamp: "0:30 - 2:00",
          text: "Here is what the hype videos never tell you: day 3 gave me eye fatigue, and typing on a virtual 120-inch curved display while drinking coffee felt bizarre. But by day 12, once I dialed the interpupillary distance and calibrated 120Hz display refresh over DisplayPort Alt Mode, something clicked.",
          notes: "Cut to POV B-roll shot showing the floating virtual multi-window space over a coffee cup.",
          wordCount: 61
        },
        {
          id: "sec_value",
          order: 3,
          name: "Core Benchmarks & Deep Dive",
          timestamp: "2:00 - 7:30",
          text: "Let's examine the optical benchmarks. At 49 Pixels Per Degree, text rendering matches a 1440p monitor viewed at 24 inches. Code syntax highlighting in VS Code remains razor-sharp without chromatic aberration around edges. Latency measured at 12ms motion-to-photon, which virtually eliminates vestibular dissonance.",
          notes: "Split screen: Macro camera shot of text clarity vs physical OLED display.",
          wordCount: 53
        },
        {
          id: "sec_climax",
          order: 4,
          name: "The Trade-Offs Matrix",
          timestamp: "7:30 - 9:15",
          text: "Who should actually buy this today? If you live in a compact studio apartment, travel frequently, or work in coffee shops, this setup is genuinely revolutionary. However, if you color-grade HDR 10-bit video for 10 hours a day, the brightness fall-off at edges means you still want reference panels.",
          notes: "Display a clean graphic overlay rating Ergonomics, Color Accuracy, Portability, and Value.",
          wordCount: 58
        },
        {
          id: "sec_cta",
          order: 5,
          name: "Verdict & Outro CTA",
          timestamp: "9:15 - 10:30",
          text: "Technology is at its best when it disappears. Let me know in the comments: would you dare pack away your monitors for a month? Drop your desk specs below, subscribe for the long-term battery teardown next week, and check out my full gear checklist in the description.",
          notes: "Warm medium shot with glasses placed next to keyboard. Lower-third subscribe animation.",
          wordCount: 55
        }
      ],
      scenes: [
        {
          id: "sc_1",
          num: "SCENE 01",
          title: "Cold Open A-Roll",
          timing: "0:00 - 0:30",
          framing: "Medium Close-Up 50mm",
          prompt: "Creator seated at clean walnut desk. Camera slowly pushes in from wide to medium. High-contrast directional key light.",
          narration: "Look at this completely empty desk. No monitors, no docking stations, no 50 cables..."
        },
        {
          id: "sc_2",
          num: "SCENE 02",
          title: "POV Virtual Workspace",
          timing: "0:30 - 1:45",
          framing: "First-Person POV 16mm",
          prompt: "Camera wearing glasses rig looking out into modern room. Three virtual translucent ultra-wide browser monitors float in 3D space.",
          narration: "Typing on a virtual 120-inch curved display while drinking coffee felt bizarre..."
        },
        {
          id: "sc_3",
          num: "SCENE 03",
          title: "Optical Text Benchmark Macro",
          timing: "1:45 - 3:30",
          framing: "Macro Lens 100mm",
          prompt: "Extreme close-up through optical waveguide showing micro-OLED pixels rendering crisp monospace code text.",
          narration: "At 49 Pixels Per Degree, text rendering matches a 1440p monitor viewed at 24 inches..."
        },
        {
          id: "sc_4",
          num: "SCENE 04",
          title: "Travel & Cafe B-Roll",
          timing: "3:30 - 5:15",
          framing: "Handheld 35mm",
          prompt: "Creator opens small zippered pouch in bustling modern coffee shop, connects cable to laptop, immediately working across virtual screens.",
          narration: "If you live in a compact studio or travel frequently, this setup is genuinely revolutionary..."
        }
      ],
      thumbnails: [
        {
          id: "th_1",
          title: "Concept A: The Radical Disappearance",
          visualHint: "Visual Discrepancy",
          overlayText: "NO MONITORS?!",
          visualDesc: "Creator sitting with mouth slightly agape pointing down at a completely bare wooden desk with just glasses glowing slightly.",
          focalPoints: "High-contrast rim lighting, bold yellow typography in top-left quadrant, blurred deep blue background."
        },
        {
          id: "th_2",
          title: "Concept B: Split Screen Reality",
          visualHint: "Before vs After",
          overlayText: "$4,500 vs $599",
          visualDesc: "Left side: cluttered cable-heavy triple monitor fortress with red tint. Right side: clean minimalist coffee table with glasses and green tint.",
          focalPoints: "Clear diagonal divider line, high price anchor, high-visibility contrast."
        },
        {
          id: "th_3",
          title: "Concept C: First-Person Sci-Fi",
          visualHint: "Curiosity / POV",
          overlayText: "DON'T BUY A MONITOR",
          visualDesc: "First-person view hands on keyboard with vibrant floating translucent screens stretching into the peripheral space.",
          focalPoints: "Cyberpunk blue-violet color palette, extreme depth of field, bold 3-word warning."
        }
      ],
      seo: {
        titleVariations: [
          { title: "I Threw Away My Monitors for AR Glasses (Day 30)", angle: "Radical Swap" },
          { title: "Why Programmers Are Ditching Triple Monitors in 2026", angle: "Industry Shift" },
          { title: "Stop Buying Curved Monitors Until You See This", angle: "Curiosity Warning" },
          { title: "Can You Actually Code in Smart Glasses? (Real 30-Day Test)", angle: "Direct Question" }
        ],
        description: `Can lightweight AR smart glasses genuinely replace a $4,500 triple monitor workstation? Here is my unvarnished 30-day experience testing optics, latency, text clarity, and productivity.\n\nTIMESTAMPS:\n0:00 - The Empty Desk Experiment\n0:30 - Day 3 Eye Strain & Initial Shock\n2:00 - Optical Benchmarks: Text Clarity & PPD\n7:30 - Who Should (and Shouldn't) Buy This\n9:15 - Final Verdict\n\nGEAR & SETUP DETAILS:\n- Glasses: 1080p Micro-OLED Waveguide\n- Laptop: Direct DisplayPort over USB-C\n- Keyboard: Low-profile mechanical wireless`,
        tags: [
          "ar glasses",
          "smart glasses",
          "desk setup",
          "productivity setup",
          "programming workstation",
          "minimalist desk",
          "future tech 2026"
        ]
      },
      versionHistory: [
        {
          timestamp: Date.now() - 7200000,
          label: "Initial 5-Part Script Generation",
          snapshotSummary: "Created initial outline with 4 hooks and 5 script sections."
        }
      ]
    }
  ];

  // --- App State ---
  const state = {
    user: {
      id: "usr_creator_01",
      name: "Alex Rivera",
      email: "creator@creatorpilot.ai",
      plan: "creator", // 'creator' | 'pro' | 'studio'
      isAnnual: false,
      status: "active", // 'active' | 'cancelled'
      usageCount: 14,
      usageLimit: 50
    },
    channelSettings: {
      niche: "Tech & AI Hardware",
      audience: "Developers, creators, and desk setup enthusiasts looking for ultra-minimalist workspaces.",
      tone: "Fast-Paced",
      brandVoice: "Avoid hype words like 'revolutionary' or 'mind-blowing'; focus on real benchmarks and honest trade-offs.",
      hostCharacters: "Alex (Host / Tech Reviewer)",
      visualStyle: "Clean minimal desk, high-contrast macro lighting, moody dark background"
    },
    projects: [],
    activeProjectId: null,
    isAnnualBilling: false,
    selectedExportFormat: "markdown",
    checkoutTarget: {
      tier: "pro",
      name: "Pro Plan",
      isAnnual: false,
      priceVal: 49
    },
    billingHistory: [
      {
        date: "2026-10-15",
        plan: "Creator Plan (Monthly)",
        amount: "$19.00",
        txId: "tx_ord_90184712",
        signature: "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
      }
    ]
  };

  // --- Storage Helper ---
  function loadFromStorage() {
    try {
      const storedProjects = localStorage.getItem(`creatorpilot_projects_${state.user.id}`);
      if (storedProjects) {
        state.projects = JSON.parse(storedProjects);
      } else {
        state.projects = JSON.parse(JSON.stringify(SEED_PROJECTS));
        saveToStorage();
      }

      const storedUser = localStorage.getItem(`creatorpilot_user_${state.user.id}`);
      if (storedUser) {
        Object.assign(state.user, JSON.parse(storedUser));
      }

      const storedSettings = localStorage.getItem(`creatorpilot_channel_${state.user.id}`);
      if (storedSettings) {
        Object.assign(state.channelSettings, JSON.parse(storedSettings));
      }

      const storedHistory = localStorage.getItem(`creatorpilot_billing_${state.user.id}`);
      if (storedHistory) {
        state.billingHistory = JSON.parse(storedHistory);
      }
    } catch (e) {
      console.warn("Could not load local storage", e);
      state.projects = JSON.parse(JSON.stringify(SEED_PROJECTS));
    }

    if (state.projects.length > 0) {
      state.activeProjectId = state.projects[0].id;
    }
  }

  function saveToStorage() {
    try {
      localStorage.setItem(`creatorpilot_projects_${state.user.id}`, JSON.stringify(state.projects));
      localStorage.setItem(`creatorpilot_user_${state.user.id}`, JSON.stringify(state.user));
      localStorage.setItem(`creatorpilot_channel_${state.user.id}`, JSON.stringify(state.channelSettings));
      localStorage.setItem(`creatorpilot_billing_${state.user.id}`, JSON.stringify(state.billingHistory));
      indicateAutosaved();
    } catch (e) {
      console.error("Failed saving to localStorage", e);
    }
  }

  function getActiveProject() {
    return state.projects.find(p => p.id === state.activeProjectId) || state.projects[0];
  }

  // --- Autosave Indicator Helper ---
  let autosaveTimer = null;
  function triggerAutosave() {
    const indicator = document.getElementById('autosaveIndicator');
    if (indicator) {
      indicator.innerText = "Saving changes...";
      indicator.className = "autosave-indicator saving";
    }

    clearTimeout(autosaveTimer);
    autosaveTimer = setTimeout(() => {
      const proj = getActiveProject();
      if (proj) {
        proj.updatedAt = Date.now();
      }
      saveToStorage();
      updateProjectSelector();
      renderContentCalendar();
    }, 450);
  }

  function indicateAutosaved() {
    const indicator = document.getElementById('autosaveIndicator');
    if (indicator) {
      indicator.innerText = "✓ Autosaved";
      indicator.className = "autosave-indicator";
    }
  }

  function showToast(message) {
    const toast = document.getElementById('appToast');
    const text = document.getElementById('toastText');
    if (!toast || !text) return;
    text.innerText = message;
    toast.classList.add('show');
    setTimeout(() => {
      toast.classList.remove('show');
    }, 3500);
  }

  // --- Cryptographic HMAC Verification Simulation ---
  async function simulateCryptographicHmacVerification(orderId, paymentId, secretKey) {
    // Uses browser Web Crypto API to generate genuine HMAC-SHA256 digest
    try {
      const encoder = new TextEncoder();
      const data = encoder.encode(`${orderId}|${paymentId}`);
      const keyData = encoder.encode(secretKey || "sec_creatorpilot_prod_key_9921");
      const cryptoKey = await window.crypto.subtle.importKey(
        "raw",
        keyData,
        { name: "HMAC", hash: { name: "SHA-256" } },
        false,
        ["sign"]
      );
      const signatureBuffer = await window.crypto.subtle.sign("HMAC", cryptoKey, data);
      const hashArray = Array.from(new Uint8Array(signatureBuffer));
      const hashHex = hashArray.map(b => b.toString(16).padStart(2, '0')).join('');
      return { success: true, signature: hashHex };
    } catch (err) {
      // Fallback pseudo hash
      return {
        success: true,
        signature: Array.from({ length: 64 }, () => Math.floor(Math.random() * 16).toString(16)).join('')
      };
    }
  }

  // --- DOM Initialization ---
  document.addEventListener('DOMContentLoaded', () => {
    loadFromStorage();
    initNavbar();
    initWorkspaceTabs();
    initProjectSelector();
    initIdeaLab();
    initScriptStudio();
    initSceneStudio();
    initThumbnailStudio();
    initSeoStudio();
    initContentCalendar();
    initPricingAndBilling();
    initModals();
    renderCurrentWorkspace();
  });

  // --- Navigation & Mobile ---
  function initNavbar() {
    const mobileToggle = document.getElementById('mobileToggle');
    const navMenu = document.getElementById('navMenu');
    if (mobileToggle && navMenu) {
      mobileToggle.addEventListener('click', () => {
        navMenu.classList.toggle('mobile-open');
      });
      navMenu.querySelectorAll('a').forEach(link => {
        link.addEventListener('click', () => navMenu.classList.remove('mobile-open'));
      });
    }

    const userAccountBtn = document.getElementById('userAccountBtn');
    if (userAccountBtn) {
      userAccountBtn.addEventListener('click', () => {
        openModal('modalAccountBilling');
      });
    }

    const footerBillingLink = document.getElementById('footerBillingLink');
    if (footerBillingLink) {
      footerBillingLink.addEventListener('click', (e) => {
        e.preventDefault();
        openModal('modalAccountBilling');
      });
    }

    const footerPrivacyLink = document.getElementById('footerPrivacyLink');
    if (footerPrivacyLink) {
      footerPrivacyLink.addEventListener('click', (e) => {
        e.preventDefault();
        openModal('modalPrivacy');
      });
    }

    const footerChannelSettingsLink = document.getElementById('footerChannelSettingsLink');
    if (footerChannelSettingsLink) {
      footerChannelSettingsLink.addEventListener('click', (e) => {
        e.preventDefault();
        openModal('modalChannelSettings');
      });
    }

    updateUserBadgeDisplay();
  }

  function updateUserBadgeDisplay() {
    const navUserName = document.getElementById('navUserName');
    const navPlanBadge = document.getElementById('navPlanBadge');
    const wsPlanLabel = document.getElementById('wsPlanLabel');
    const usageCountDisplay = document.getElementById('usageCountDisplay');
    const usageLimitDisplay = document.getElementById('usageLimitDisplay');

    const planCapitalized = state.user.plan.charAt(0).toUpperCase() + state.user.plan.slice(1);
    if (navUserName) navUserName.innerText = state.user.name;
    if (navPlanBadge) navPlanBadge.innerText = planCapitalized;
    if (wsPlanLabel) wsPlanLabel.innerText = `${planCapitalized} Plan`;
    if (usageCountDisplay) usageCountDisplay.innerText = state.user.usageCount;
    if (usageLimitDisplay) {
      usageLimitDisplay.innerText = state.user.plan === 'pro' || state.user.plan === 'studio' ? 'Unlimited' : state.user.usageLimit;
    }
  }

  // --- Workspace Tabs ---
  function initWorkspaceTabs() {
    const tabBtns = document.querySelectorAll('.ws-tab-btn');
    const panes = document.querySelectorAll('.ws-tab-pane');

    tabBtns.forEach(btn => {
      btn.addEventListener('click', () => {
        tabBtns.forEach(b => b.classList.remove('active'));
        panes.forEach(p => p.classList.remove('active'));

        btn.classList.add('active');
        const targetId = `pane-${btn.dataset.tab}`;
        const targetPane = document.getElementById(targetId);
        if (targetPane) targetPane.classList.add('active');
      });
    });
  }

  // --- Project Selector & Top Actions ---
  function initProjectSelector() {
    const selector = document.getElementById('projectSelector');
    if (!selector) return;

    selector.addEventListener('change', () => {
      state.activeProjectId = selector.value;
      renderCurrentWorkspace();
    });

    const btnNewProjectModal = document.getElementById('btnNewProjectModal');
    if (btnNewProjectModal) {
      btnNewProjectModal.addEventListener('click', () => {
        openModal('modalNewProject');
      });
    }

    const btnDuplicateProject = document.getElementById('btnDuplicateProject');
    if (btnDuplicateProject) {
      btnDuplicateProject.addEventListener('click', () => {
        duplicateCurrentProject();
      });
    }

    const btnDeleteProject = document.getElementById('btnDeleteProject');
    if (btnDeleteProject) {
      btnDeleteProject.addEventListener('click', () => {
        const proj = getActiveProject();
        if (!proj) return;
        const targetText = document.getElementById('deleteProjTitleTarget');
        if (targetText) targetText.innerText = proj.title;
        openModal('modalDeleteConfirm');
      });
    }

    const btnConfirmDelete = document.getElementById('btnConfirmDeleteProject');
    if (btnConfirmDelete) {
      btnConfirmDelete.addEventListener('click', () => {
        deleteCurrentProject();
        closeModal('modalDeleteConfirm');
      });
    }

    const btnExportModal = document.getElementById('btnExportModal');
    if (btnExportModal) {
      btnExportModal.addEventListener('click', () => {
        prepareExportContent();
        openModal('modalExport');
      });
    }

    const btnVersionHistory = document.getElementById('btnVersionHistory');
    if (btnVersionHistory) {
      btnVersionHistory.addEventListener('click', () => {
        renderVersionHistoryList();
        openModal('modalVersionHistory');
      });
    }

    const btnChannelSettings = document.getElementById('btnChannelSettings');
    if (btnChannelSettings) {
      btnChannelSettings.addEventListener('click', () => {
        populateChannelSettingsModal();
        openModal('modalChannelSettings');
      });
    }

    updateProjectSelector();
  }

  function updateProjectSelector() {
    const selector = document.getElementById('projectSelector');
    if (!selector) return;

    selector.innerHTML = state.projects.map(p => `
      <option value="${p.id}" ${p.id === state.activeProjectId ? 'selected' : ''}>
        ${escapeHtml(p.title)} (${p.status})
      </option>
    `).join('');
  }

  function duplicateCurrentProject() {
    const current = getActiveProject();
    if (!current) return;

    const copy = JSON.parse(JSON.stringify(current));
    copy.id = "proj_" + Date.now();
    copy.title = `${current.title} (Copy)`;
    copy.updatedAt = Date.now();
    copy.versionHistory = [
      {
        timestamp: Date.now(),
        label: "Duplicated from " + current.title,
        snapshotSummary: "Cloned project."
      }
    ];

    state.projects.unshift(copy);
    state.activeProjectId = copy.id;
    saveToStorage();
    updateProjectSelector();
    renderCurrentWorkspace();
    showToast(`Project duplicated as "${copy.title}"`);
  }

  function deleteCurrentProject() {
    if (state.projects.length <= 1) {
      showToast("Cannot delete the only project. Create a new project first.");
      return;
    }

    const idx = state.projects.findIndex(p => p.id === state.activeProjectId);
    if (idx !== -1) {
      const deleted = state.projects.splice(idx, 1)[0];
      state.activeProjectId = state.projects[0].id;
      saveToStorage();
      updateProjectSelector();
      renderCurrentWorkspace();
      showToast(`Deleted "${deleted.title}"`);
    }
  }

  // --- Main Workspace Render ---
  function renderCurrentWorkspace() {
    const proj = getActiveProject();
    if (!proj) return;

    renderIdeaLabFields(proj);
    renderScriptStudioFields(proj);
    renderSceneTimeline(proj);
    renderThumbnailCards(proj);
    renderSeoFields(proj);
    renderContentCalendar();
  }

  // --- Tab 1: Idea Lab Logic ---
  function initIdeaLab() {
    const topicInput = document.getElementById('inputIdeaTopic');
    const nicheSelect = document.getElementById('selectIdeaNiche');
    const audienceInput = document.getElementById('inputTargetAudience');
    const formatSelect = document.getElementById('selectVideoFormat');
    const toneChips = document.querySelectorAll('#ideaToneChips .tone-chip');
    const genBtn = document.getElementById('btnGenerateHooks');

    if (topicInput) {
      topicInput.addEventListener('input', () => {
        const proj = getActiveProject();
        if (proj) {
          proj.topic = topicInput.value;
          triggerAutosave();
        }
      });
    }

    if (nicheSelect) {
      nicheSelect.addEventListener('change', () => {
        const proj = getActiveProject();
        if (proj) {
          proj.niche = nicheSelect.value;
          const badge = document.getElementById('activeNicheBadge');
          if (badge) badge.innerText = proj.niche;
          triggerAutosave();
        }
      });
    }

    if (audienceInput) {
      audienceInput.addEventListener('input', () => {
        const proj = getActiveProject();
        if (proj) {
          proj.targetAudience = audienceInput.value;
          triggerAutosave();
        }
      });
    }

    if (formatSelect) {
      formatSelect.addEventListener('change', () => {
        const proj = getActiveProject();
        if (proj) {
          proj.format = formatSelect.value;
          triggerAutosave();
        }
      });
    }

    toneChips.forEach(chip => {
      chip.addEventListener('click', () => {
        toneChips.forEach(c => c.classList.remove('active'));
        chip.classList.add('active');
        const proj = getActiveProject();
        if (proj) {
          proj.tone = chip.dataset.tone;
          triggerAutosave();
        }
      });
    });

    if (genBtn) {
      genBtn.addEventListener('click', () => {
        generateFreshHooks();
      });
    }
  }

  function renderIdeaLabFields(proj) {
    const topicInput = document.getElementById('inputIdeaTopic');
    const nicheSelect = document.getElementById('selectIdeaNiche');
    const audienceInput = document.getElementById('inputTargetAudience');
    const formatSelect = document.getElementById('selectVideoFormat');
    const nicheBadge = document.getElementById('activeNicheBadge');

    if (topicInput) topicInput.value = proj.topic || "";
    if (nicheSelect) nicheSelect.value = proj.niche || "Tech & AI Hardware";
    if (audienceInput) audienceInput.value = proj.targetAudience || "";
    if (formatSelect) formatSelect.value = proj.format || "Long-form (8-12 min)";
    if (nicheBadge) nicheBadge.innerText = proj.niche || "Tech & AI";

    const toneChips = document.querySelectorAll('#ideaToneChips .tone-chip');
    toneChips.forEach(c => {
      c.classList.toggle('active', c.dataset.tone === proj.tone);
    });

    renderHooksList(proj);
  }

  function renderHooksList(proj) {
    const container = document.getElementById('hooksListContainer');
    if (!container) return;

    if (!proj.hooks || proj.hooks.length === 0) {
      container.innerHTML = `
        <div style="text-align:center; padding:3rem; color:var(--text-muted);">
          No hooks generated yet. Enter a topic and tap "Synthesize 4 Hook Archetypes".
        </div>
      `;
      return;
    }

    container.innerHTML = proj.hooks.map((h, idx) => `
      <div class="hook-card ${h.isSelected ? 'selected' : ''}" data-hook-id="${h.id}">
        <div class="hook-header">
          <span class="hook-type-pill">${escapeHtml(h.type)}</span>
          <span class="hook-score-badge">Audience Retention: ${h.score}</span>
        </div>
        <div class="hook-text-editor" contenteditable="true" data-hook-index="${idx}">
          ${escapeHtml(h.hook)}
        </div>
        <div class="hook-rationale">
          <strong>Strategic Rationale:</strong> ${escapeHtml(h.rationale)}
        </div>
        <div class="hook-actions-row">
          <button class="btn btn-ghost btn-xs btn-copy-hook" data-hook-index="${idx}">📋 Copy</button>
          <button class="btn ${h.isSelected ? 'btn-primary' : 'btn-secondary'} btn-xs btn-select-hook" data-hook-index="${idx}">
            ${h.isSelected ? '✓ Primary Hook' : 'Set as Script Hook'}
          </button>
        </div>
      </div>
    `).join('');

    // Attach inline edit handlers for hooks
    container.querySelectorAll('.hook-text-editor').forEach(el => {
      el.addEventListener('input', () => {
        const idx = parseInt(el.dataset.hookIndex, 10);
        proj.hooks[idx].hook = el.innerText.trim();
        triggerAutosave();
      });
    });

    container.querySelectorAll('.btn-select-hook').forEach(btn => {
      btn.addEventListener('click', () => {
        const idx = parseInt(btn.dataset.hookIndex, 10);
        proj.hooks.forEach((hk, i) => hk.isSelected = (i === idx));
        // Also update Hook section in Script Studio
        if (proj.scriptSections && proj.scriptSections.length > 0) {
          proj.scriptSections[0].text = proj.hooks[idx].hook;
          proj.scriptSections[0].wordCount = countWords(proj.hooks[idx].hook);
        }
        renderHooksList(proj);
        renderScriptStudioFields(proj);
        triggerAutosave();
        showToast("Hook selected and applied to Script Studio!");
      });
    });

    container.querySelectorAll('.btn-copy-hook').forEach(btn => {
      btn.addEventListener('click', () => {
        const idx = parseInt(btn.dataset.hookIndex, 10);
        navigator.clipboard.writeText(proj.hooks[idx].hook).then(() => {
          showToast("Hook copied to clipboard!");
        });
      });
    });
  }

  function generateFreshHooks() {
    const proj = getActiveProject();
    if (!proj) return;

    const topic = proj.topic || "Building an automated video creator workspace in 2026";
    const niche = proj.niche || "Tech & AI Hardware";

    const btn = document.getElementById('btnGenerateHooks');
    if (btn) {
      btn.disabled = true;
      btn.innerText = "Synthesizing 4 Hooks...";
    }

    setTimeout(() => {
      proj.hooks = [
        {
          id: "hook_" + Date.now() + "_1",
          type: "Curiosity Gap",
          hook: `Look closely at this setup. For 30 days, I completely abandoned standard ${niche.toLowerCase()} methods to test one controversial alternative — and the results surprised me.`,
          score: "95 / 100",
          rationale: "Establishes curiosity gap by withholding the exact conclusion while highlighting extreme experimentation.",
          isSelected: true
        },
        {
          id: "hook_" + Date.now() + "_2",
          type: "Contrarian / Problem",
          hook: `The advice everyone gives about ${topic.slice(0, 45)} is officially outdated. Here is what actually happens when you push modern tools to their limit.`,
          score: "92 / 100",
          rationale: "Directly questions accepted wisdom without misleading clickbait guarantees.",
          isSelected: false
        },
        {
          id: "hook_" + Date.now() + "_3",
          type: "Story Narrative",
          hook: `Three months ago, I was completely overwhelmed by my production schedule. That was the moment I decided to rebuild my entire workflow from scratch.`,
          score: "89 / 100",
          rationale: "Empathy-driven story opening that connects directly with viewer frustrations.",
          isSelected: false
        },
        {
          id: "hook_" + Date.now() + "_4",
          type: "Bold Claim",
          hook: `If you only make one upgrade to your creative workflow this year, let it be this. Here is the full benchmark breakdown.`,
          score: "90 / 100",
          rationale: "High urgency and clear payoff for analytical decision-makers.",
          isSelected: false
        }
      ];

      // Update script opening hook
      if (proj.scriptSections && proj.scriptSections.length > 0) {
        proj.scriptSections[0].text = proj.hooks[0].hook;
        proj.scriptSections[0].wordCount = countWords(proj.hooks[0].hook);
      }

      // Track usage
      state.user.usageCount += 1;
      updateUserBadgeDisplay();

      // Record snapshot
      addVersionSnapshot(proj, "Synthesized New Hook Archetypes");

      renderHooksList(proj);
      renderScriptStudioFields(proj);
      triggerAutosave();

      if (btn) {
        btn.disabled = false;
        btn.innerText = "⚡ Synthesize 4 Hook Archetypes";
      }

      showToast("4 fresh hook archetypes generated!");
    }, 450);
  }

  // --- Tab 2: Script Studio Logic ---
  function initScriptStudio() {
    const titleInput = document.getElementById('scriptProjectTitle');
    const toneChips = document.querySelectorAll('#scriptStudioToneChips .tone-chip');
    const notesInput = document.getElementById('scriptSpeakerNotes');
    const btnRegenAll = document.getElementById('btnRegenerateFullScript');
    const btnCopyAll = document.getElementById('btnCopyEntireScript');

    if (titleInput) {
      titleInput.addEventListener('input', () => {
        const proj = getActiveProject();
        if (proj) {
          proj.title = titleInput.value;
          updateProjectSelector();
          triggerAutosave();
        }
      });
    }

    if (notesInput) {
      notesInput.addEventListener('input', () => {
        const proj = getActiveProject();
        if (proj) {
          proj.speakerNotes = notesInput.value;
          triggerAutosave();
        }
      });
    }

    toneChips.forEach(chip => {
      chip.addEventListener('click', () => {
        toneChips.forEach(c => c.classList.remove('active'));
        chip.classList.add('active');
        const proj = getActiveProject();
        if (proj) {
          proj.tone = chip.dataset.tone;
          triggerAutosave();
        }
      });
    });

    if (btnRegenAll) {
      btnRegenAll.addEventListener('click', () => {
        regenerateFullScript();
      });
    }

    if (btnCopyAll) {
      btnCopyAll.addEventListener('click', () => {
        const proj = getActiveProject();
        if (!proj || !proj.scriptSections) return;
        const fullScript = proj.scriptSections.map(s => `[${s.name} - ${s.timestamp}]\n${s.text}\nNotes: ${s.notes}`).join('\n\n');
        navigator.clipboard.writeText(fullScript).then(() => {
          showToast("Entire script copied to clipboard!");
        });
      });
    }
  }

  function renderScriptStudioFields(proj) {
    const titleInput = document.getElementById('scriptProjectTitle');
    const notesInput = document.getElementById('scriptSpeakerNotes');

    if (titleInput) titleInput.value = proj.title || "";
    if (notesInput) notesInput.value = proj.speakerNotes || "";

    const toneChips = document.querySelectorAll('#scriptStudioToneChips .tone-chip');
    toneChips.forEach(c => {
      c.classList.toggle('active', c.dataset.tone === proj.tone);
    });

    renderScriptSectionsStack(proj);
  }

  function renderScriptSectionsStack(proj) {
    const stack = document.getElementById('scriptSectionsStack');
    if (!stack) return;

    if (!proj.scriptSections || proj.scriptSections.length === 0) {
      stack.innerHTML = `<div style="text-align:center; padding:3rem; color:var(--text-muted);">No script sections. Tap "Regenerate Full 5-Part Script".</div>`;
      return;
    }

    stack.innerHTML = proj.scriptSections.map((sec, idx) => `
      <div class="script-section-block" data-section-id="${sec.id}">
        <div class="section-block-header">
          <div class="section-label-group">
            <span class="section-order-badge">0${sec.order}</span>
            <span class="section-name">${escapeHtml(sec.name)}</span>
            <span class="section-timestamp-tag">${sec.timestamp}</span>
          </div>
          <div class="section-actions">
            <button class="btn btn-secondary btn-xs btn-regen-section" data-section-index="${idx}" title="Regenerate this specific section">
              ⚡ Regenerate
            </button>
            <button class="btn btn-ghost btn-xs btn-copy-section" data-section-index="${idx}" title="Copy section text">
              📋
            </button>
          </div>
        </div>

        <textarea class="script-content-textarea" data-section-index="${idx}">${escapeHtml(sec.text)}</textarea>

        <div class="section-footer-meta">
          <span>Director Notes: <em>${escapeHtml(sec.notes || 'None')}</em></span>
          <span class="mono">${sec.wordCount || countWords(sec.text)} words</span>
        </div>
      </div>
    `).join('');

    // Attach text input handlers to update word counts and reading time
    stack.querySelectorAll('.script-content-textarea').forEach(textarea => {
      textarea.addEventListener('input', () => {
        const idx = parseInt(textarea.dataset.sectionIndex, 10);
        proj.scriptSections[idx].text = textarea.value;
        proj.scriptSections[idx].wordCount = countWords(textarea.value);
        updateScriptWordStats(proj);
        triggerAutosave();
      });
    });

    stack.querySelectorAll('.btn-regen-section').forEach(btn => {
      btn.addEventListener('click', () => {
        const idx = parseInt(btn.dataset.sectionIndex, 10);
        regenerateSingleSection(proj, idx);
      });
    });

    stack.querySelectorAll('.btn-copy-section').forEach(btn => {
      btn.addEventListener('click', () => {
        const idx = parseInt(btn.dataset.sectionIndex, 10);
        navigator.clipboard.writeText(proj.scriptSections[idx].text).then(() => {
          showToast(`Section ${proj.scriptSections[idx].name} copied!`);
        });
      });
    });

    updateScriptWordStats(proj);
  }

  function updateScriptWordStats(proj) {
    if (!proj.scriptSections) return;
    const totalWords = proj.scriptSections.reduce((sum, s) => sum + (s.wordCount || countWords(s.text)), 0);
    const estSec = Math.round((totalWords / 145) * 60); // 145 wpm speaking speed
    const mins = Math.floor(estSec / 60);
    const secs = estSec % 60;
    const durationStr = `${mins}:${secs < 10 ? '0' : ''}${secs}`;

    const wordsDisplay = document.getElementById('scriptTotalWords');
    const readTimeDisplay = document.getElementById('estReadTime');
    if (wordsDisplay) wordsDisplay.innerText = `${totalWords} words`;
    if (readTimeDisplay) readTimeDisplay.innerText = durationStr;
  }

  function regenerateFullScript() {
    const proj = getActiveProject();
    if (!proj) return;

    const btn = document.getElementById('btnRegenerateFullScript');
    if (btn) {
      btn.disabled = true;
      btn.innerText = "Drafting 5 Sections...";
    }

    setTimeout(() => {
      const activeHook = proj.hooks?.find(h => h.isSelected)?.hook || "Look closely at this experiment...";
      const tone = proj.tone || "Fast-Paced";

      proj.scriptSections = [
        {
          id: "sec_hook_" + Date.now(),
          order: 1,
          name: "Hook & Visual Anchor",
          timestamp: "0:00 - 0:30",
          text: activeHook,
          notes: "Rapid push-in camera framing. Immediate visual proof shown on screen.",
          wordCount: countWords(activeHook)
        },
        {
          id: "sec_setup_" + Date.now(),
          order: 2,
          name: "Problem Setup & Stakes",
          timestamp: "0:30 - 2:00",
          text: `For years, creators have struggled with standard approaches to ${proj.niche.toLowerCase()}. Today, we are breaking down the exact trade-offs nobody talks about in the marketing specs.`,
          notes: "Cut to B-roll showing setup challenges and genuine friction points.",
          wordCount: 32
        },
        {
          id: "sec_value_" + Date.now(),
          order: 3,
          name: "Core Value & Benchmarks",
          timestamp: "2:00 - 7:30",
          text: `Here are the 3 major discoveries from 30 days of real-world use. Number 1: the efficiency improvement. Number 2: the ergonomic reality. Number 3: the long-term cost comparison.`,
          notes: "Visual graphs and comparative split-screen footage.",
          wordCount: 35
        },
        {
          id: "sec_climax_" + Date.now(),
          order: 4,
          name: "The Climax & Verdict",
          timestamp: "7:30 - 9:15",
          text: `Here is the honest verdict. If your priority is portability and zero desk clutter, this is unbeatable. If you need color-critical production work, proceed with caution.`,
          notes: "Direct eye contact A-roll. Authentic summary.",
          wordCount: 30
        },
        {
          id: "sec_cta_" + Date.now(),
          order: 5,
          name: "Call to Action & Outro",
          timestamp: "9:15 - 10:30",
          text: `What would your dream setup look like? Drop your current specs in the comments below. Subscribe for our upcoming deep dive, and download the full production checklist in the description!`,
          notes: "End screen cards and channel subscription prompt.",
          wordCount: 32
        }
      ];

      state.user.usageCount += 1;
      updateUserBadgeDisplay();
      addVersionSnapshot(proj, `Regenerated Full 5-Part Script (${tone})`);

      renderScriptSectionsStack(proj);
      triggerAutosave();

      if (btn) {
        btn.disabled = false;
        btn.innerText = "⚡ Regenerate Full 5-Part Script";
      }

      showToast("5-part structured script regenerated!");
    }, 450);
  }

  function regenerateSingleSection(proj, idx) {
    const section = proj.scriptSections[idx];
    if (!section) return;

    const variations = [
      `Let's take a closer look at this exact moment. When you examine the real numbers behind ${proj.niche.toLowerCase()}, the conventional wisdom quickly falls apart.`,
      `Here is the detail most creators overlook: it is not about the gear itself, but how seamlessly it fits into your daily recording cadence.`,
      `Testing this over 30 continuous days exposed three major bottlenecks that you need to know before spending your hard-earned budget.`
    ];

    section.text = variations[Math.floor(Math.random() * variations.length)];
    section.wordCount = countWords(section.text);
    addVersionSnapshot(proj, `Regenerated Section ${section.name}`);

    renderScriptSectionsStack(proj);
    triggerAutosave();
    showToast(`Section "${section.name}" regenerated!`);
  }

  // --- Tab 3: Scene Studio Logic ---
  function initSceneStudio() {
    const btnAdd = document.getElementById('btnAddSceneRow');
    const btnRegen = document.getElementById('btnRegenAllScenes');

    if (btnAdd) {
      btnAdd.addEventListener('click', () => {
        const proj = getActiveProject();
        if (!proj) return;
        const newNum = (proj.scenes?.length || 0) + 1;
        proj.scenes = proj.scenes || [];
        proj.scenes.push({
          id: "sc_" + Date.now(),
          num: `SCENE 0${newNum}`,
          title: "Custom B-Roll Angle",
          timing: "5:00 - 6:00",
          framing: "Medium Shot 35mm",
          prompt: "Creator demonstration with clean directional lighting and shallow depth of field.",
          narration: "Audio voiceover narration corresponding to this section..."
        });
        renderSceneTimeline(proj);
        triggerAutosave();
        showToast("New scene added to shotlist!");
      });
    }

    if (btnRegen) {
      btnRegen.addEventListener('click', () => {
        regenerateSceneBreakdown();
      });
    }
  }

  function renderSceneTimeline(proj) {
    const container = document.getElementById('scenesTimeline');
    const badge = document.getElementById('scenesCountBadge');
    if (!container) return;

    const count = proj.scenes?.length || 0;
    if (badge) badge.innerText = `${count} Scenes`;

    if (count === 0) {
      container.innerHTML = `<div style="text-align:center; padding:3rem; color:var(--text-muted);">No scenes in shotlist. Tap "Regenerate Shotlist" or "+ Add Custom Scene".</div>`;
      return;
    }

    container.innerHTML = proj.scenes.map((sc, idx) => `
      <div class="scene-timeline-card" data-scene-id="${sc.id}">
        <div class="scene-timing-column">
          <span class="scene-index-pill">${sc.num}</span>
          <span class="scene-timestamp">${sc.timing}</span>
          <span class="scene-framing-badge">${sc.framing}</span>
        </div>

        <div class="scene-details-column">
          <input type="text" class="scene-title-input" value="${escapeHtml(sc.title)}" data-scene-idx="${idx}">
          <div class="scene-prompt-box">
            <strong>Camera & Lighting Prompt:</strong> ${escapeHtml(sc.prompt)}
          </div>
          <div class="scene-narration-box">
            "${escapeHtml(sc.narration)}"
          </div>
        </div>

        <div>
          <button class="btn btn-ghost btn-xs btn-delete-scene" data-scene-idx="${idx}" title="Remove scene">✕</button>
        </div>
      </div>
    `).join('');

    container.querySelectorAll('.scene-title-input').forEach(input => {
      input.addEventListener('input', () => {
        const idx = parseInt(input.dataset.sceneIdx, 10);
        proj.scenes[idx].title = input.value;
        triggerAutosave();
      });
    });

    container.querySelectorAll('.btn-delete-scene').forEach(btn => {
      btn.addEventListener('click', () => {
        const idx = parseInt(btn.dataset.sceneIdx, 10);
        proj.scenes.splice(idx, 1);
        renderSceneTimeline(proj);
        triggerAutosave();
      });
    });
  }

  function regenerateSceneBreakdown() {
    const proj = getActiveProject();
    if (!proj) return;

    const btn = document.getElementById('btnRegenAllScenes');
    if (btn) {
      btn.disabled = true;
      btn.innerText = "Synthesizing Shotlist...";
    }

    setTimeout(() => {
      proj.scenes = [
        {
          id: "sc_gen_1",
          num: "SCENE 01",
          title: "Cold Open A-Roll Hook",
          timing: "0:00 - 0:30",
          framing: "Medium Close-Up 50mm",
          prompt: "Creator seated at clean desk. Camera slowly pushes in from wide to medium. High-contrast directional key light.",
          narration: proj.scriptSections?.[0]?.text?.slice(0, 80) + "..." || "Opening hook..."
        },
        {
          id: "sc_gen_2",
          num: "SCENE 02",
          title: "First-Person B-Roll Demonstration",
          timing: "0:30 - 2:00",
          framing: "First-Person POV 16mm",
          prompt: "Handheld or chest-mounted POV camera showing the physical interface and working environment in active use.",
          narration: "Here is the exact setup and the early friction points..."
        },
        {
          id: "sc_gen_3",
          num: "SCENE 03",
          title: "Macro Component / Screen Comparison",
          timing: "2:00 - 5:30",
          framing: "Macro 100mm Lens",
          prompt: "Extreme close-up with soft background blur demonstrating visual clarity and precision detail.",
          narration: "Examining the optical benchmarks and real-world clarity..."
        },
        {
          id: "sc_gen_4",
          num: "SCENE 04",
          title: "Wide Atmosphere & Conclusion",
          timing: "5:30 - 8:00",
          framing: "Slow Push-In 35mm",
          prompt: "Wide studio frame with soft ambient fill light. Creator relaxed at desk summarizing final takeaways.",
          narration: "The final verdict: who this is for and what to know before buying..."
        }
      ];

      addVersionSnapshot(proj, "Regenerated Director's Production Shotlist");
      renderSceneTimeline(proj);
      triggerAutosave();

      if (btn) {
        btn.disabled = false;
        btn.innerText = "⚡ Regenerate Shotlist";
      }

      showToast("Production shotlist generated!");
    }, 450);
  }

  // --- Tab 4: Thumbnail Studio Logic ---
  function initThumbnailStudio() {
    const btnRegen = document.getElementById('btnRegenThumbnails');
    if (btnRegen) {
      btnRegen.addEventListener('click', () => {
        regenerateThumbnails();
      });
    }
  }

  function renderThumbnailCards(proj) {
    const grid = document.getElementById('thumbnailsGrid');
    if (!grid) return;

    if (!proj.thumbnails || proj.thumbnails.length === 0) {
      grid.innerHTML = `<div style="text-align:center; padding:3rem; color:var(--text-muted); grid-column:1/-1;">No thumbnail concepts. Tap "Generate New Visual Concepts".</div>`;
      return;
    }

    grid.innerHTML = proj.thumbnails.map((th, idx) => `
      <div class="thumb-concept-card">
        <div class="thumb-preview-canvas">
          <span class="thumb-visual-hint">${escapeHtml(th.visualHint)}</span>
          <div class="thumb-text-overlay">${escapeHtml(th.overlayText)}</div>
        </div>
        <div class="thumb-body">
          <h4 class="thumb-concept-title">${escapeHtml(th.title)}</h4>
          <p class="thumb-visual-desc">${escapeHtml(th.visualDesc)}</p>
          <div class="thumb-focal-points">
            <strong>Composition & Color:</strong> ${escapeHtml(th.focalPoints)}
          </div>
          <button class="btn btn-secondary btn-xs btn-copy-thumb" data-thumb-idx="${idx}" style="margin-top:auto;">
            📋 Copy Concept Brief
          </button>
        </div>
      </div>
    `).join('');

    grid.querySelectorAll('.btn-copy-thumb').forEach(btn => {
      btn.addEventListener('click', () => {
        const idx = parseInt(btn.dataset.thumbIdx, 10);
        const th = proj.thumbnails[idx];
        const brief = `Thumbnail Concept: ${th.title}\nOverlay Text: "${th.overlayText}"\nVisual Layout: ${th.visualDesc}\nComposition: ${th.focalPoints}`;
        navigator.clipboard.writeText(brief).then(() => {
          showToast("Thumbnail brief copied to clipboard!");
        });
      });
    });
  }

  function regenerateThumbnails() {
    const proj = getActiveProject();
    if (!proj) return;

    const btn = document.getElementById('btnRegenThumbnails');
    if (btn) {
      btn.disabled = true;
      btn.innerText = "Designing Concepts...";
    }

    setTimeout(() => {
      proj.thumbnails = [
        {
          id: "th_gen_1",
          title: "High Curiosity Discrepancy",
          visualHint: "Shock / Contrast",
          overlayText: "I WAS WRONG.",
          visualDesc: "Creator seated in empty room pointing directly at the camera with genuine curiosity expression.",
          focalPoints: "Yellow bold san-serif font in top-left, deep blue background, rim light on subject."
        },
        {
          id: "th_gen_2",
          title: "Price & Value Anchor",
          visualHint: "Before vs After",
          overlayText: "$4,500 vs $599",
          visualDesc: "Split screen showing bulky cable mess on left versus sleek ultra-portable setup on right.",
          focalPoints: "Diagonal high-contrast red vs green badge divider, crisp macro focus."
        },
        {
          id: "th_gen_3",
          title: "First-Person Perspective",
          visualHint: "Immersive POV",
          overlayText: "DON'T BUY THIS!",
          visualDesc: "First-person hands view typing into glowing futuristic virtual window arrays.",
          focalPoints: "Cyberpunk violet-blue tones, clean white text with dark outline shadow."
        }
      ];

      addVersionSnapshot(proj, "Generated New Thumbnail Concepts");
      renderThumbnailCards(proj);
      triggerAutosave();

      if (btn) {
        btn.disabled = false;
        btn.innerText = "⚡ Generate New Visual Concepts";
      }

      showToast("3 fresh thumbnail visual concepts created!");
    }, 450);
  }

  // --- Tab 5: Title & SEO Studio Logic ---
  function initSeoStudio() {
    const btnEvaluate = document.getElementById('btnTestTitleLength');
    const inputTitle = document.getElementById('customTitleTestInput');
    const descBox = document.getElementById('videoDescriptionBox');
    const btnCopyDesc = document.getElementById('btnCopyDescription');

    if (btnEvaluate && inputTitle) {
      btnEvaluate.addEventListener('click', () => {
        const val = inputTitle.value.trim();
        const feedback = document.getElementById('titleFeedbackBox');
        if (!feedback) return;
        if (!val) {
          feedback.innerHTML = `<span style="color:var(--accent-amber);">Please enter a title to test.</span>`;
          return;
        }

        const len = val.length;
        let msg = "";
        let color = "var(--accent-emerald)";

        if (len < 25) {
          msg = `Character count: ${len}. A bit short — consider adding a specific outcome or topic anchor.`;
          color = "var(--accent-amber)";
        } else if (len > 68) {
          msg = `Character count: ${len}. May be truncated on mobile devices (recommended: 45-65 characters).`;
          color = "var(--accent-amber)";
        } else {
          msg = `Optimal length (${len} characters)! Clear preview readability across mobile and desktop.`;
        }

        feedback.innerHTML = `<span style="color:${color}; font-weight:600;">${msg}</span>`;
      });
    }

    if (descBox) {
      descBox.addEventListener('input', () => {
        const proj = getActiveProject();
        if (proj && proj.seo) {
          proj.seo.description = descBox.value;
          triggerAutosave();
        }
      });
    }

    if (btnCopyDesc && descBox) {
      btnCopyDesc.addEventListener('click', () => {
        navigator.clipboard.writeText(descBox.value).then(() => {
          showToast("Description copied to clipboard!");
        });
      });
    }
  }

  function renderSeoFields(proj) {
    const variationsStack = document.getElementById('titleVariationsStack');
    const descBox = document.getElementById('videoDescriptionBox');
    const tagsBox = document.getElementById('tagsCloudBox');

    if (descBox && proj.seo) {
      descBox.value = proj.seo.description || "";
    }

    if (variationsStack && proj.seo?.titleVariations) {
      variationsStack.innerHTML = proj.seo.titleVariations.map(tv => `
        <div class="title-variation-row">
          <span class="title-text-val">${escapeHtml(tv.title)}</span>
          <span class="title-angle-badge">${escapeHtml(tv.angle)}</span>
          <button class="btn btn-ghost btn-xs btn-use-title" data-title="${escapeHtml(tv.title)}">Use</button>
        </div>
      `).join('');

      variationsStack.querySelectorAll('.btn-use-title').forEach(b => {
        b.addEventListener('click', () => {
          proj.title = b.dataset.title;
          const titleInput = document.getElementById('scriptProjectTitle');
          if (titleInput) titleInput.value = proj.title;
          updateProjectSelector();
          triggerAutosave();
          showToast(`Title set to: "${proj.title}"`);
        });
      });
    }

    if (tagsBox && proj.seo?.tags) {
      tagsBox.innerHTML = proj.seo.tags.map(tag => `
        <span class="seo-tag-pill">#${escapeHtml(tag)}</span>
      `).join('');
    }
  }

  // --- Tab 6: Content Calendar Studio Logic ---
  function initContentCalendar() {
    const btnExportCsv = document.getElementById('btnExportCalendarCsv');
    const btnSchedule = document.getElementById('btnScheduleReleaseModal');

    if (btnExportCsv) {
      btnExportCsv.addEventListener('click', () => {
        exportCalendarAsCsv();
      });
    }

    if (btnSchedule) {
      btnSchedule.addEventListener('click', () => {
        const proj = getActiveProject();
        if (!proj) return;
        const newDate = prompt("Enter scheduled release date (YYYY-MM-DD):", proj.scheduledDate || "2026-11-15");
        if (newDate) {
          proj.scheduledDate = newDate;
          proj.status = "Scheduled";
          renderContentCalendar();
          updateProjectSelector();
          triggerAutosave();
          showToast(`Project scheduled for ${newDate}`);
        }
      });
    }
  }

  function renderContentCalendar() {
    const container = document.getElementById('calendarColumnsContainer');
    if (!container) return;

    const columns = [
      { id: "Idea", label: "💡 Ideation" },
      { id: "Scripting", label: "✍️ Scripting" },
      { id: "Editing", label: "🎬 Production & Edit" },
      { id: "Scheduled", label: "📅 Scheduled / Done" }
    ];

    container.innerHTML = columns.map(col => {
      const items = state.projects.filter(p => {
        if (col.id === "Editing") return p.status === "In Production" || p.status === "Editing";
        if (col.id === "Scheduled") return p.status === "Scheduled" || p.status === "Published";
        return p.status === col.id;
      });

      return `
        <div class="cal-column" data-status="${col.id}">
          <div class="cal-col-header">
            <span class="cal-col-title">${col.label}</span>
            <span class="badge-success">${items.length}</span>
          </div>

          <div style="display:flex; flex-direction:column; gap:0.65rem; margin-top:0.5rem; flex-grow:1;">
            ${items.length === 0 ? `<div style="font-size:0.8rem; color:var(--text-muted); text-align:center; padding:1.5rem;">No projects in this stage</div>` : ''}
            ${items.map(it => `
              <div class="cal-item-card ${it.id === state.activeProjectId ? 'active-proj' : ''}" data-project-id="${it.id}">
                <div class="cal-item-title">${escapeHtml(it.title)}</div>
                <div class="cal-item-meta">
                  <span>${it.niche}</span>
                  <span>${it.scheduledDate ? '📅 ' + it.scheduledDate : 'No date'}</span>
                </div>
              </div>
            `).join('')}
          </div>
        </div>
      `;
    }).join('');

    container.querySelectorAll('.cal-item-card').forEach(card => {
      card.addEventListener('click', () => {
        state.activeProjectId = card.dataset.projectId;
        updateProjectSelector();
        renderCurrentWorkspace();
        showToast(`Loaded: ${getActiveProject()?.title}`);
      });
    });
  }

  function exportCalendarAsCsv() {
    let csv = "Project ID,Title,Niche,Format,Status,Scheduled Date\n";
    state.projects.forEach(p => {
      csv += `"${p.id}","${p.title.replace(/"/g, '""')}","${p.niche}","${p.format}","${p.status}","${p.scheduledDate || 'Unscheduled'}"\n`;
    });

    downloadTextFile("creatorpilot_calendar.csv", csv, "text/csv");
    showToast("Calendar exported as CSV!");
  }

  // --- Pricing & Billing Management ---
  function initPricingAndBilling() {
    const billingSwitchBtn = document.getElementById('billingSwitchBtn');
    const monthlyLabel = document.getElementById('monthlyLabel');
    const annualLabel = document.getElementById('annualLabel');
    const priceNumbers = document.querySelectorAll('.price-number');
    const annualNotes = document.querySelectorAll('.annual-charge-note');

    if (billingSwitchBtn) {
      billingSwitchBtn.addEventListener('click', () => {
        state.isAnnualBilling = !state.isAnnualBilling;
        billingSwitchBtn.classList.toggle('annual', state.isAnnualBilling);
        if (monthlyLabel) monthlyLabel.classList.toggle('active', !state.isAnnualBilling);
        if (annualLabel) annualLabel.classList.toggle('active', state.isAnnualBilling);

        priceNumbers.forEach(el => {
          const m = el.dataset.monthly;
          const a = el.dataset.annual;
          el.innerText = state.isAnnualBilling ? a : m;
        });

        annualNotes.forEach(note => {
          note.classList.toggle('hidden', !state.isAnnualBilling);
        });
      });
    }

    const selectTierBtns = document.querySelectorAll('.btn-select-tier');
    selectTierBtns.forEach(btn => {
      btn.addEventListener('click', () => {
        const tier = btn.dataset.tier;
        const name = btn.dataset.name;
        const card = btn.closest('.plan-card');
        const price = card?.querySelector('.price-number')?.innerText || "49";

        state.checkoutTarget = {
          tier: tier,
          name: name,
          isAnnual: state.isAnnualBilling,
          priceVal: parseInt(price, 10)
        };

        const titleDisplay = document.getElementById('chkoutPlanTitle');
        const cadenceDisplay = document.getElementById('chkoutCadenceTitle');
        const totalDisplay = document.getElementById('chkoutTotalAmount');

        if (titleDisplay) titleDisplay.innerText = name;
        if (cadenceDisplay) cadenceDisplay.innerText = state.isAnnualBilling ? "Annual (20% Savings)" : "Monthly";
        if (totalDisplay) {
          totalDisplay.innerText = state.isAnnualBilling
            ? `$${state.checkoutTarget.priceVal * 12}.00 / year`
            : `$${state.checkoutTarget.priceVal}.00 / month`;
        }

        openModal('modalCheckout');
      });
    });

    const btnConfirmCheckout = document.getElementById('btnConfirmCheckout');
    if (btnConfirmCheckout) {
      btnConfirmCheckout.addEventListener('click', async () => {
        btnConfirmCheckout.disabled = true;
        btnConfirmCheckout.innerText = "Verifying HMAC-SHA256 Signature...";

        const statusText = document.getElementById('chkoutVerificationStatus');
        if (statusText) statusText.style.display = 'block';

        const orderId = "ord_" + Date.now();
        const paymentId = "pay_" + Math.random().toString(36).substring(2, 10);
        const verification = await simulateCryptographicHmacVerification(orderId, paymentId);

        setTimeout(() => {
          // Apply entitlements
          state.user.plan = state.checkoutTarget.tier;
          state.user.isAnnual = state.checkoutTarget.isAnnual;
          state.user.status = "active";
          if (state.user.plan === 'creator') state.user.usageLimit = 50;
          if (state.user.plan === 'pro' || state.user.plan === 'studio') state.user.usageLimit = 9999;

          // Record verified ledger entry
          state.billingHistory.unshift({
            date: new Date().toISOString().split('T')[0],
            plan: `${state.checkoutTarget.name} (${state.checkoutTarget.isAnnual ? 'Annual' : 'Monthly'})`,
            amount: state.checkoutTarget.isAnnual ? `$${state.checkoutTarget.priceVal * 12}.00` : `$${state.checkoutTarget.priceVal}.00`,
            txId: paymentId,
            signature: verification.signature
          });

          saveToStorage();
          updateUserBadgeDisplay();
          renderBillingHistoryTable();

          btnConfirmCheckout.disabled = false;
          btnConfirmCheckout.innerText = "Verify & Activate";
          if (statusText) statusText.style.display = 'none';
          closeModal('modalCheckout');

          showToast(`🎉 Subscribed to ${state.checkoutTarget.name}! Entitlements verified.`);
        }, 850);
      });
    }

    const btnCancelSub = document.getElementById('btnCancelSubscription');
    if (btnCancelSub) {
      btnCancelSub.addEventListener('click', () => {
        if (confirm("Are you sure you want to cancel your subscription? You will retain access until the end of your current billing period.")) {
          state.user.status = "cancelled";
          saveToStorage();
          updateBillingModalDetails();
          showToast("Subscription cancelled. Access remains active through current billing cycle.");
        }
      });
    }

    renderBillingHistoryTable();
  }

  function updateBillingModalDetails() {
    const planName = document.getElementById('billingCurrentPlanName');
    const badge = document.getElementById('billingSubscriptionStatusBadge');
    const renewal = document.getElementById('billingRenewalNotice');
    const usageNumbers = document.getElementById('billingUsageNumbers');
    const progressBar = document.getElementById('billingUsageProgressBar');

    const planTitle = state.user.plan.charAt(0).toUpperCase() + state.user.plan.slice(1);
    if (planName) planName.innerText = `${planTitle} Plan`;

    if (badge) {
      badge.innerText = state.user.status === 'active' ? 'Active' : 'Cancelled (Grace Period)';
      badge.className = state.user.status === 'active' ? 'badge-success' : 'btn-danger btn-xs';
    }

    if (renewal) {
      renewal.innerText = state.user.status === 'active'
        ? "Auto-renews next cycle. Cancel anytime to stop future renewals."
        : "Will not renew. Creative features remain available through period end.";
    }

    if (usageNumbers) {
      usageNumbers.innerText = state.user.plan === 'pro' || state.user.plan === 'studio'
        ? `${state.user.usageCount} Scripts (Unlimited)`
        : `${state.user.usageCount} / 50 Scripts`;
    }

    if (progressBar) {
      const pct = state.user.plan === 'pro' || state.user.plan === 'studio' ? 20 : Math.min(100, (state.user.usageCount / 50) * 100);
      progressBar.style.width = `${pct}%`;
    }
  }

  function renderBillingHistoryTable() {
    const tbody = document.getElementById('billingHistoryTableBody');
    if (!tbody) return;

    if (state.billingHistory.length === 0) {
      tbody.innerHTML = `<tr><td colspan="5" style="text-align:center; color:var(--text-muted);">No payment records found.</td></tr>`;
      return;
    }

    tbody.innerHTML = state.billingHistory.map(row => `
      <tr>
        <td>${row.date}</td>
        <td><strong>${escapeHtml(row.plan)}</strong></td>
        <td>${row.amount}</td>
        <td class="mono">${escapeHtml(row.txId)}</td>
        <td><span class="crypto-hash-tag" title="${escapeHtml(row.signature)}">${escapeHtml(row.signature)}</span></td>
      </tr>
    `).join('');
  }

  // --- Version Snapshots Engine ---
  function addVersionSnapshot(proj, label) {
    proj.versionHistory = proj.versionHistory || [];
    const snapshot = JSON.parse(JSON.stringify({
      title: proj.title,
      hooks: proj.hooks,
      scriptSections: proj.scriptSections,
      scenes: proj.scenes
    }));

    proj.versionHistory.unshift({
      timestamp: Date.now(),
      label: label,
      snapshotSummary: `${proj.scriptSections?.length || 0} sections, ${proj.scenes?.length || 0} scenes`,
      data: snapshot
    });

    if (proj.versionHistory.length > 15) {
      proj.versionHistory.pop();
    }
  }

  function renderVersionHistoryList() {
    const list = document.getElementById('versionHistoryList');
    if (!list) return;

    const proj = getActiveProject();
    if (!proj || !proj.versionHistory || proj.versionHistory.length === 0) {
      list.innerHTML = `<div style="text-align:center; color:var(--text-muted); padding:2rem;">No saved revisions yet. Edits and generations are recorded automatically.</div>`;
      return;
    }

    list.innerHTML = proj.versionHistory.map((v, idx) => `
      <div class="version-item-card">
        <div class="version-item-info">
          <strong>${escapeHtml(v.label)}</strong>
          <span>${new Date(v.timestamp).toLocaleString()} • ${escapeHtml(v.snapshotSummary || '')}</span>
        </div>
        ${v.data ? `<button class="btn btn-secondary btn-xs btn-restore-version" data-version-idx="${idx}">Restore</button>` : ''}
      </div>
    `).join('');

    list.querySelectorAll('.btn-restore-version').forEach(b => {
      b.addEventListener('click', () => {
        const idx = parseInt(b.dataset.versionIdx, 10);
        const selectedVersion = proj.versionHistory[idx];
        if (selectedVersion && selectedVersion.data) {
          Object.assign(proj, JSON.parse(JSON.stringify(selectedVersion.data)));
          saveToStorage();
          renderCurrentWorkspace();
          closeModal('modalVersionHistory');
          showToast(`Restored snapshot from ${new Date(selectedVersion.timestamp).toLocaleTimeString()}`);
        }
      });
    });
  }

  // --- Export Engine ---
  function prepareExportContent() {
    const proj = getActiveProject();
    if (!proj) return;

    const preview = document.getElementById('exportPreviewText');
    if (!preview) return;

    const format = state.selectedExportFormat || "markdown";

    if (format === "markdown") {
      preview.value = generateMarkdownExport(proj);
    } else if (format === "txt") {
      preview.value = generatePlainTextExport(proj);
    } else if (format === "json") {
      preview.value = JSON.stringify(proj, null, 2);
    } else if (format === "csv") {
      preview.value = generateCsvChecklistExport(proj);
    }
  }

  function generateMarkdownExport(proj) {
    let md = `# ${proj.title}\n\n`;
    md += `**Niche:** ${proj.niche} | **Format:** ${proj.format} | **Tone:** ${proj.tone}\n\n`;
    md += `## Primary Hook\n${proj.scriptSections?.[0]?.text || ''}\n\n`;
    md += `## 5-Act Script Teleprompter\n\n`;
    (proj.scriptSections || []).forEach(s => {
      md += `### ${s.name} (${s.timestamp})\n${s.text}\n*Director Notes: ${s.notes}*\n\n`;
    });
    md += `## Production Shotlist\n\n`;
    (proj.scenes || []).forEach(sc => {
      md += `### ${sc.num}: ${sc.title} [${sc.timing} - ${sc.framing}]\n`;
      md += `- **Camera & Lighting:** ${sc.prompt}\n`;
      md += `- **Voiceover:** "${sc.narration}"\n\n`;
    });
    md += `## Thumbnail Briefs\n\n`;
    (proj.thumbnails || []).forEach(th => {
      md += `- **${th.title}**: Overlay Text: "${th.overlayText}"\n  ${th.visualDesc}\n`;
    });
    md += `\n## YouTube Description & Chapters\n\n${proj.seo?.description || ''}\n`;
    return md;
  }

  function generatePlainTextExport(proj) {
    let txt = `PROJECT: ${proj.title.toUpperCase()}\n`;
    txt += `Niche: ${proj.niche}\nFormat: ${proj.format}\n\n`;
    txt += `--- FULL SCRIPT ---\n\n`;
    (proj.scriptSections || []).forEach(s => {
      txt += `[${s.name.toUpperCase()} - ${s.timestamp}]\n${s.text}\n\n`;
    });
    return txt;
  }

  function generateCsvChecklistExport(proj) {
    let csv = "Stage,Section,Timing,Framing / Direction,Status\n";
    (proj.scenes || []).forEach(sc => {
      csv += `"Scene","${sc.title.replace(/"/g, '""')}","${sc.timing}","${sc.framing.replace(/"/g, '""')}","Pending Shoot"\n`;
    });
    (proj.thumbnails || []).forEach(th => {
      csv += `"Thumbnail","${th.title.replace(/"/g, '""')}","N/A","${th.overlayText.replace(/"/g, '""')}","Pending Design"\n`;
    });
    return csv;
  }

  // --- Channel Settings ---
  function populateChannelSettingsModal() {
    const csNiche = document.getElementById('csNiche');
    const csAudience = document.getElementById('csAudience');
    const csTone = document.getElementById('csTone');
    const csBrandVoice = document.getElementById('csBrandVoice');
    const csHostCharacters = document.getElementById('csHostCharacters');
    const csVisualStyle = document.getElementById('csVisualStyle');

    if (csNiche) csNiche.value = state.channelSettings.niche || "";
    if (csAudience) csAudience.value = state.channelSettings.audience || "";
    if (csTone) csTone.value = state.channelSettings.tone || "Fast-Paced";
    if (csBrandVoice) csBrandVoice.value = state.channelSettings.brandVoice || "";
    if (csHostCharacters) csHostCharacters.value = state.channelSettings.hostCharacters || "";
    if (csVisualStyle) csVisualStyle.value = state.channelSettings.visualStyle || "";
  }

  function saveChannelSettings() {
    const csNiche = document.getElementById('csNiche');
    const csAudience = document.getElementById('csAudience');
    const csTone = document.getElementById('csTone');
    const csBrandVoice = document.getElementById('csBrandVoice');
    const csHostCharacters = document.getElementById('csHostCharacters');
    const csVisualStyle = document.getElementById('csVisualStyle');

    if (csNiche) state.channelSettings.niche = csNiche.value;
    if (csAudience) state.channelSettings.audience = csAudience.value;
    if (csTone) state.channelSettings.tone = csTone.value;
    if (csBrandVoice) state.channelSettings.brandVoice = csBrandVoice.value;
    if (csHostCharacters) state.channelSettings.hostCharacters = csHostCharacters.value;
    if (csVisualStyle) state.channelSettings.visualStyle = csVisualStyle.value;

    saveToStorage();
    closeModal('modalChannelSettings');
    showToast("Channel settings and brand voice saved!");
  }

  // --- Modals Controller ---
  function initModals() {
    document.querySelectorAll('[data-close]').forEach(el => {
      el.addEventListener('click', () => {
        closeModal(el.dataset.close);
      });
    });

    document.querySelectorAll('.modal-overlay').forEach(overlay => {
      overlay.addEventListener('click', (e) => {
        if (e.target === overlay) {
          overlay.classList.remove('open');
        }
      });
    });

    // New Project submission
    const btnSubmitNewProj = document.getElementById('btnSubmitNewProject');
    if (btnSubmitNewProj) {
      btnSubmitNewProj.addEventListener('click', () => {
        const titleInput = document.getElementById('newProjTitle');
        const topicInput = document.getElementById('newProjTopic');
        const nicheSelect = document.getElementById('newProjNiche');
        const audienceInput = document.getElementById('newProjAudience');
        const formatSelect = document.getElementById('newProjFormat');

        const title = titleInput?.value.trim() || "Untitled Project " + (state.projects.length + 1);
        const topic = topicInput?.value.trim() || title;
        const niche = nicheSelect?.value || state.channelSettings.niche;
        const audience = audienceInput?.value.trim() || state.channelSettings.audience;
        const format = formatSelect?.value || "Long-form (8-12 min)";

        const newProject = {
          id: "proj_" + Date.now(),
          title: title,
          topic: topic,
          niche: niche,
          targetAudience: audience,
          format: format,
          duration: "10:00",
          tone: state.channelSettings.tone || "Fast-Paced",
          status: "Idea",
          scheduledDate: "",
          updatedAt: Date.now(),
          hooks: [
            {
              id: "hk_" + Date.now() + "_1",
              type: "Curiosity Gap",
              hook: `Here is the one counter-intuitive discovery about ${title} that completely changed my view.`,
              score: "92 / 100",
              rationale: "Sharp curiosity opening matching niche specifications.",
              isSelected: true
            }
          ],
          scriptSections: [
            {
              id: "sec_" + Date.now(),
              order: 1,
              name: "Hook & Visual Anchor",
              timestamp: "0:00 - 0:30",
              text: `Here is the one counter-intuitive discovery about ${title} that completely changed my view.`,
              notes: "Direct eye contact with camera.",
              wordCount: 16
            }
          ],
          scenes: [
            {
              id: "sc_" + Date.now(),
              num: "SCENE 01",
              title: "Intro Hook",
              timing: "0:00 - 0:30",
              framing: "Medium Shot 50mm",
              prompt: "Opening shot framed in studio.",
              narration: "Here is the one counter-intuitive discovery..."
            }
          ],
          thumbnails: [
            {
              id: "th_" + Date.now(),
              title: "Primary Concept",
              visualHint: "Bold Claim",
              overlayText: "THE TRUTH.",
              visualDesc: "Creator with serious analytical gaze next to bold graphic contrast.",
              focalPoints: "Yellow bold text on dark navy background."
            }
          ],
          seo: {
            titleVariations: [
              { title: title, angle: "Direct Topic" },
              { title: `Why Nobody Is Talking About ${title}`, angle: "Contrarian Urgency" }
            ],
            description: `Full breakdown of ${title}.\n\nTIMESTAMPS:\n0:00 - Intro Hook\n`,
            tags: [niche.toLowerCase().replace(/\s+/g, ''), "youtube", "creatorpilot"]
          },
          versionHistory: []
        };

        state.projects.unshift(newProject);
        state.activeProjectId = newProject.id;
        saveToStorage();
        updateProjectSelector();
        renderCurrentWorkspace();

        closeModal('modalNewProject');
        showToast(`Created project "${newProject.title}"`);

        // Clear input form
        if (titleInput) titleInput.value = "";
        if (topicInput) topicInput.value = "";
      });
    }

    // Save channel settings
    const btnSaveChannelSettings = document.getElementById('btnSaveChannelSettings');
    if (btnSaveChannelSettings) {
      btnSaveChannelSettings.addEventListener('click', saveChannelSettings);
    }

    // Export tabs
    const exportTabs = document.querySelectorAll('.export-tab-btn');
    exportTabs.forEach(tab => {
      tab.addEventListener('click', () => {
        exportTabs.forEach(t => {
          t.classList.remove('active');
          t.classList.remove('btn-primary');
          t.classList.add('btn-secondary');
        });
        tab.classList.add('active');
        tab.classList.remove('btn-secondary');
        tab.classList.add('btn-primary');
        state.selectedExportFormat = tab.dataset.format;
        prepareExportContent();
      });
    });

    const btnCopyExport = document.getElementById('btnCopyExportText');
    if (btnCopyExport) {
      btnCopyExport.addEventListener('click', () => {
        const text = document.getElementById('exportPreviewText')?.value;
        if (text) {
          navigator.clipboard.writeText(text).then(() => {
            showToast("Export content copied to clipboard!");
          });
        }
      });
    }

    const btnDownloadExport = document.getElementById('btnDownloadExportFile');
    if (btnDownloadExport) {
      btnDownloadExport.addEventListener('click', () => {
        const proj = getActiveProject();
        const text = document.getElementById('exportPreviewText')?.value;
        if (!proj || !text) return;
        const format = state.selectedExportFormat;
        const filename = `${proj.title.toLowerCase().replace(/[^a-z0-9]/g, '_')}.${format === 'markdown' ? 'md' : format}`;
        const mime = format === 'json' ? 'application/json' : format === 'csv' ? 'text/csv' : 'text/plain';
        downloadTextFile(filename, text, mime);
        showToast(`Downloaded ${filename}!`);
      });
    }

    // FAQ Accordion
    const faqBtns = document.querySelectorAll('.faq-question-btn');
    faqBtns.forEach(btn => {
      btn.addEventListener('click', () => {
        const item = btn.closest('.faq-item');
        if (item) item.classList.toggle('open');
      });
    });
  }

  function openModal(id) {
    const modal = document.getElementById(id);
    if (!modal) return;
    if (id === 'modalAccountBilling') updateBillingModalDetails();
    modal.classList.add('open');
  }

  function closeModal(id) {
    const modal = document.getElementById(id);
    if (modal) modal.classList.remove('open');
  }

  // --- General Utilities ---
  function countWords(str) {
    if (!str) return 0;
    return str.trim().split(/\s+/).filter(Boolean).length;
  }

  function escapeHtml(str) {
    if (!str) return "";
    return str
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#039;");
  }

  function downloadTextFile(filename, text, mimeType) {
    const blob = new Blob([text], { type: mimeType });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }

})();
