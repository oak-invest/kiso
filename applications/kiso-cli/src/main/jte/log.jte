@param com.oakinvest.kiso.cli.model.page.LogPage page

@template.layouts.default(
pageContext = page.context(),
pageMetadata = page.metadata(),
pageContent = @`

    <!-- Header ==================================================================================================== -->
    @template.log-header(page = page)

    <!-- Content =================================================================================================== -->
    <article class="kiso-content">
        ${page.htmlContent()}
    </article>

    <!-- Markdown file link ======================================================================================== -->
    @template.components.common.markdown-file(markdownFilename = page.metadata().markdownFilename())

`)
