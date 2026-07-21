(() => {
    const maximumResults = 8;

    function textValue(value) {
        if (value === null || value === undefined) {
            return "";
        }
        return String(value);
    }

    function resultUrl(baseUrl, documentUrl) {
        return baseUrl + textValue(documentUrl);
    }

    function clearResults(resultsElement) {
        resultsElement.replaceChildren();
        resultsElement.classList.add("hidden");
    }

    function appendResult(resultsElement, baseUrl, result) {
        const link = document.createElement("a");
        link.href = resultUrl(baseUrl, result.url);
        link.className = "kiso-search-result rounded-field block px-3 py-2 hover:bg-base-200";

        const title = document.createElement("span");
        title.className = "kiso-search-result-title";
        title.textContent = textValue(result.title || result.id);

        const description = document.createElement("span");
        description.className = "kiso-search-result-description";
        description.textContent = textValue(result.description || result.url);

        link.append(title, description);
        resultsElement.append(link);
    }

    async function loadSearchIndex(container) {
        const response = await fetch(container.dataset.searchIndexUrl);
        if (!response.ok) {
            throw new Error("Failed to load search index");
        }
        const searchIndex = await response.json();
        const documents = Array.isArray(searchIndex.documents) ? searchIndex.documents : [];
        const miniSearch = new MiniSearch({
            fields: ["title", "description", "tags", "body"],
            storeFields: ["id", "url", "title", "description", "tags"]
        });
        miniSearch.addAll(documents);
        return miniSearch;
    }

    function renderResults(resultsElement, baseUrl, results) {
        resultsElement.replaceChildren();
        if (results.length === 0) {
            clearResults(resultsElement);
            return;
        }
        results.slice(0, maximumResults).forEach(result => appendResult(resultsElement, baseUrl, result));
        resultsElement.classList.remove("hidden");
    }

    function initializeSearch(container) {
        const button = container.querySelector(".kiso-search-button");
        const input = container.querySelector(".kiso-search-input");
        const resultsElement = container.querySelector(".kiso-search-results");
        const baseUrl = container.dataset.searchResultBaseUrl || "";
        let miniSearchPromise;

        button.addEventListener("click", () => {
            container.classList.add("kiso-search-open");
            button.setAttribute("aria-expanded", "true");
            input.focus();
        });

        input.addEventListener("input", async () => {
            const query = input.value.trim();
            if (query.length === 0) {
                clearResults(resultsElement);
                return;
            }
            try {
                if (!miniSearchPromise) {
                    miniSearchPromise = loadSearchIndex(container);
                }
                const miniSearch = await miniSearchPromise;
                const results = miniSearch.search(query, { prefix: true, fuzzy: 0.2 });
                renderResults(resultsElement, baseUrl, results);
            } catch (error) {
                clearResults(resultsElement);
            }
        });

        input.addEventListener("keydown", event => {
            if (event.key === "Escape") {
                input.value = "";
                clearResults(resultsElement);
                container.classList.remove("kiso-search-open");
                button.setAttribute("aria-expanded", "false");
                button.focus();
            }
        });
    }

    document.querySelectorAll(".kiso-search").forEach(initializeSearch);
})();
