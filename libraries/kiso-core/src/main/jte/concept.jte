@import java.time.format.DateTimeFormatter

@param com.oakinvest.kiso.core.renderer.model.page.ConceptPage page

@template.layouts.default(
dataTheme  = page.themeConfiguration().effectiveName(),
language = page.siteConfiguration().languageTag(),
title = page.metadata().title(),
description = page.metadata().description(),
markdownFileName = page.metadata().markdownFileName(),
assetBasePath = page.metadata().assetBasePath(),
bundleTree = page.bundleTree(),
currentHtmlPath = page.metadata().htmlPath(),
content = @`

    <!-- Header ==================================================================================================== -->
    <!--
    Render a card-like panel with rounded corners, a subtle border, a light surface background, a small shadow for
    elevation, clipped content, and spacing below it.
        overflow-hidden - Hide anything that overflows the element boundaries
        rounded-box - Apply the default box radius defined by the current DaisyUI theme
        border - Add a border with default width (1px)
        shadow-sm - Apply a small shadow for subtle elevation
    -->
    <section class="mb-8 overflow-hidden rounded-box border border-base-300 bg-base-100 shadow-sm">

        <!-- Header (Type, last update, title & description ======================================================== -->
        <div class="border-b border-base-300 bg-base-300/30 px-6 py-6 sm:px-8">

            <!-- Type ============================================================================================== -->
            <!--
                Create a horizontal flex row with vertically centered items, automatically spaced by 0.5rem,
                wrapping to additional lines on smaller screens, and leaving 1rem of spacing below the row.
            -->
            <div class="mb-4 flex flex-wrap items-center gap-2">
                <span class="badge badge-primary badge-lg">Concept</span>
                @if(page.type() != null)
                    <span class="badge badge-secondary badge-lg">${page.type()}</span>
                @endif
            </div>

            <!-- Title ============================================================================================= -->
            <!--
                Render a large bold title constrained to readable width, with tight line spacing and responsive font
                sizing (36px on mobile, 48px on larger screens).
                    leading-tight - Tight line-height. Without it, large titles often have too much vertical spacing.
                    tracking-normal - Normal letter spacing.
            -->
            @if(page.metadata().title() != null)
                <div class="max-w-4xl text-4xl font-bold leading-tight tracking-normal sm:text-5xl">
                    ${page.metadata().title()}
                </div>
            @endif

            <!-- Description ======================================================================================= -->
            <!--
                Render a description paragraph slightly below the title, constrained to a readable width, with slightly
                larger text, generous line spacing, and reduced contrast so it feels secondary to the main heading.
                    text-base-content/70 - Mix of DaisyUI + Tailwind opacity syntax.
            -->
            @if(page.metadata().description() != null)
                <p class="mt-4 max-w-3xl text-lg leading-8 text-base-content/70">
                    ${page.metadata().description()}
                </p>
            @endif

        </div>

        <!-- Resource & Tags ======================================================================================= -->
        @if(page.resource() != null || page.tags() != null)
            <!--
            Create a responsive grid layout that stacks content vertically on mobile with horizontal separators, then
            switches to a two-column layout on medium screens and above, using a wide main column and a narrower sidebar
            separated by a vertical divider.
                divide-y - Add horizontal separators between children.
                md:grid-cols-[minmax(0,1fr)_minmax(18rem,0.45fr)] - Define two columns on medium screens and above.
                md:divide-x - Instead of horizontal separators, add vertical separators.
                md:divide-y-0 - Remove horizontal separators on medium screens and above.
            -->
            <div class="grid gap-0 divide-y divide-base-300 md:grid-cols-[minmax(0,1fr)_minmax(18rem,0.45fr)] md:divide-x md:divide-y-0">

                <!-- Resource and it's link ======================================================================== -->
                @if(page.resource() != null)
                    <div class="px-6 py-5 sm:px-8">
                        <div class="text-xs font-semibold uppercase text-base-content/50">Resource</div>
                        <a class="mt-2 block break-words font-mono text-sm text-primary underline-offset-4 hover:underline"
                           href="${page.resource()}">
                            ${page.resource()}
                        </a>
                    </div>
                @endif

                <!-- Tags ========================================================================================== -->
                @if(page.tags() != null)
                    <div class="px-6 py-5 sm:px-8">
                        <div class="mb-3 text-xs font-semibold uppercase text-base-content/50">Tags</div>
                        <div class="flex flex-wrap gap-2">
                            @for(var tag : page.tags())
                                <span class="badge badge-ghost border border-base-300">${tag}</span>
                            @endfor
                        </div>
                    </div>
                @endif

            </div>
        @endif
    </section>

    <!-- Content =================================================================================================== -->
    <article class="kiso-content">
        <div class="kiso-source-file">
            <span class="kiso-source-file-label">Markdown file</span>
            <span class="kiso-source-file-name">
                <a class="link link-hover font-mono" href="${page.metadata().markdownFileName()}">
                    ${page.metadata().markdownFileName()}
                </a>
            </span>
        </div>
        ${page.htmlContent()}
    </article>

    <!-- Footer ==================================================================================================== -->
    @if(page.timestamp() != null)
        <footer class="mt-6 flex justify-end text-sm text-base-content/60">
            Last update: ${page.timestamp().format(DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mm a")) + " UTC"}
        </footer>
    @endif

`
)
