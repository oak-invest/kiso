(() => {
    const scriptElement = document.currentScript || document.querySelector("script[src*='kiso-i18n.js']");
    const fallbackLanguage = "en";

    function normalizeLanguage(language) {
        if (!language) {
            return "";
        }
        return language.toLowerCase().split("-")[0];
    }

    function availableLanguages() {
        if (!scriptElement || !scriptElement.dataset.i18nLanguages) {
            return [fallbackLanguage];
        }
        return scriptElement.dataset.i18nLanguages
                .split(",")
                .map(normalizeLanguage)
                .filter(language => language.length > 0);
    }

    function detectedLanguages() {
        const languages = [];
        if (navigator.languages) {
            navigator.languages.forEach(language => languages.push(language));
        }
        if (navigator.language) {
            languages.push(navigator.language);
        }
        languages.push(fallbackLanguage);
        return languages.map(normalizeLanguage);
    }

    function selectedLanguage() {
        const languages = availableLanguages();
        const selectedLanguage = detectedLanguages().find(language => languages.includes(language));
        if (selectedLanguage) {
            return selectedLanguage;
        }
        return fallbackLanguage;
    }

    async function loadTranslations(language) {
        if (!scriptElement || !scriptElement.dataset.i18nBaseUrl) {
            throw new Error("Missing i18n base URL");
        }
        const response = await fetch(scriptElement.dataset.i18nBaseUrl + language + ".json");
        if (!response.ok) {
            throw new Error("Failed to load translations");
        }
        return response.json();
    }

    function translatePage() {
        document.querySelectorAll("[data-i18n]").forEach(element => {
            element.textContent = i18next.t(element.dataset.i18n);
        });
        document.querySelectorAll("[data-i18n-aria-label]").forEach(element => {
            element.setAttribute("aria-label", i18next.t(element.dataset.i18nAriaLabel));
        });
        document.querySelectorAll("[data-i18n-placeholder]").forEach(element => {
            element.setAttribute("placeholder", i18next.t(element.dataset.i18nPlaceholder));
        });
    }

    async function initializeI18next() {
        const language = selectedLanguage();
        const fallbackTranslations = await loadTranslations(fallbackLanguage);
        let translations = fallbackTranslations;
        if (language !== fallbackLanguage) {
            translations = await loadTranslations(language);
        }
        await i18next.init({
            lng: language,
            fallbackLng: fallbackLanguage,
            resources: {
                [fallbackLanguage]: {
                    translation: fallbackTranslations
                },
                [language]: {
                    translation: translations
                }
            }
        });
        translatePage();
    }

    initializeI18next().catch(() => {
        console.warn("Kiso i18n initialization failed");
    });
})();
