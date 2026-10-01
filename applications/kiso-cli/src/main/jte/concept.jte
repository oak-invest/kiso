@import java.time.format.DateTimeFormatter

@param com.oakinvest.kiso.cli.model.page.ConceptPage page

@template.layouts.default(
pageContext = page.context(),
pageMetadata = page.metadata(),
pageContent = @`

    <!-- Header ==================================================================================================== -->
    @template.concept-header(page = page)

    <!-- Content =================================================================================================== -->
    <article class="kiso-content">
        ${page.htmlContent()}
    </article>

    <!-- Sources =================================================================================================== -->
    @template.concept-sources(page = page)

    <!-- Trust ===================================================================================================== -->
    @template.concept-trust(page = page)

    <!-- Computation =============================================================================================== -->
    @template.concept-computation(page = page)

    <!-- Markdown file link ======================================================================================== -->
    @template.components.common.markdown-file(markdownFilename = page.metadata().markdownFilename())

    <!-- Footer for OKF v0.1 ======================================================================================= -->
    @if(page.generatedAt() == null && page.timestamp() != null)
        <footer class="mt-6 text-sm text-base-content/60">
            <div class="flex flex-col gap-1 sm:flex-row sm:justify-end">
                <span><span class="font-semibold" data-i18n="footer.lastUpdate">Last update</span>:</span>
                <span>${page.timestamp().format(DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mm a")) + " UTC"}</span>
            </div>
        </footer>
    @endif

`)
