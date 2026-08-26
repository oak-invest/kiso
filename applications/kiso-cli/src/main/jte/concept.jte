@import com.oakinvest.kiso.core.tools.TagNormalizer
@import java.time.format.DateTimeFormatter

@param com.oakinvest.kiso.cli.model.page.ConceptPage page

@template.layouts.default(
pageContext = page.context(),
pageMetadata = page.metadata(),
pageContent = @`

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

        <!-- Header (Type, last update, title & description) ======================================================= -->
        <div class="border-b border-base-300 bg-base-300/30 px-6 py-6 sm:px-8">

            <!-- Type ============================================================================================== -->
            <!--
                Create a horizontal flex row with vertically centered items, automatically spaced by 0.5rem,
                wrapping to additional lines on smaller screens, and leaving 1rem of spacing below the row.
            -->
            <div class="mb-4 flex flex-wrap items-center gap-2">
                <span class="badge badge-primary badge-lg" data-i18n="pageKind.concept">Concept</span>
                @if(page.type() != null)
                    <span class="badge badge-secondary badge-lg">${page.type()}</span>
                @endif
                <span class="badge badge-accent badge-lg" data-okf-field="status">
                    <span data-i18n="okfMetadata.status">Status</span>: ${page.status().name().toLowerCase()}
                </span>
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
            -->
            @if(page.metadata().description() != null)
                <p class="mt-4 max-w-3xl text-lg leading-8 text-base-content/70">
                    ${page.metadata().description()}
                </p>
            @endif

            @if(page.staleAfter() != null)
                <div class="mt-6 border-t border-base-300 pt-4 text-sm text-base-content/70"
                     data-okf-field="stale-after">
                    <span class="font-semibold" data-i18n="okfMetadata.staleAfter">Stale after</span>:
                    <span>${page.staleAfter()}</span>
                </div>
            @endif

        </div>

        <!-- Resource & Tags ======================================================================================= -->
        @if(page.resource() != null || page.tags() != null)
            <!--
            Create a responsive grid layout that stacks content vertically on mobile with horizontal separators, then
            switches to a two-column layout on medium screens and above, using a wide main column and a narrower sidebar
            separated by a vertical divider.
                divide-y - Add horizontal separators between children.
                md:grid-cols - Define two columns on medium screens and above.
                md:divide-x - Instead of horizontal separators, add vertical separators.
                md:divide-y-0 - Remove horizontal separators on medium screens and above.
            -->
            <div class="grid gap-0 divide-y divide-base-300 md:grid-cols-[minmax(0,1fr)_minmax(18rem,0.45fr)] md:divide-x md:divide-y-0">

                <!-- Resource and it's link ======================================================================== -->
                @if(page.resource() != null)
                    <div class="px-6 py-5 sm:px-8">
                        <div class="text-xs font-semibold uppercase text-base-content/50" data-i18n="metadata.resource">
                            Resource
                        </div>
                        <a class="mt-2 block break-words font-mono text-sm text-primary underline-offset-4 hover:underline"
                           href="${page.resource()}">
                            ${page.resource()}
                        </a>
                    </div>
                @endif

                <!-- Tags ========================================================================================== -->
                @if(!page.tags().isEmpty())
                    <div class="px-6 py-5 sm:px-8">
                        <div class="mb-3 text-xs font-semibold uppercase text-base-content/50"
                             data-i18n="metadata.tags">Tags
                        </div>
                        <div class="flex flex-wrap gap-2">
                            @for(var tag : page.tags())
                                <a href="${page.metadata().assetBasePath()}tags/${TagNormalizer.normalize(tag)}.html">
                                    <span class="badge badge-ghost border border-base-300">${tag}</span>
                                </a>
                            @endfor
                        </div>
                    </div>
                @endif

            </div>
        @endif
    </section>

    <!-- Content =================================================================================================== -->
    <article class="kiso-content">
        ${page.htmlContent()}
        <div class="kiso-source-file">
            <span class="kiso-source-file-label" data-i18n="source.markdownFile">Markdown file</span>
            <span class="kiso-source-file-name">
                <a class="link link-hover font-mono" href="${page.metadata().markdownFilename()}">
                    ${page.metadata().markdownFilename()}
                </a>
            </span>
        </div>
    </article>

    <!-- Sources =================================================================================================== -->
    @if(!page.sources().isEmpty() || page.usageWindow() != null)
        <section class="mt-8 rounded-box border border-base-300 bg-base-100 p-6 shadow-sm sm:p-8"
                 data-okf-section="sources">
            <h2 class="text-xl font-semibold" data-i18n="okfMetadata.sources">Sources</h2>
            <hr class="mb-4">
            @if(page.usageWindow() != null)
                <dl class="mb-5 grid gap-3 text-sm sm:grid-cols-2" data-okf-field="usage-window">
                    @if(page.usageWindow().from() != null)
                        <div>
                            <dt class="text-xs font-semibold uppercase text-base-content/50"
                                data-i18n="okfMetadata.usageWindowFrom">Usage window from
                            </dt>
                            <dd class="mt-1">${page.usageWindow().from()}</dd>
                        </div>
                    @endif
                    @if(page.usageWindow().to() != null)
                        <div>
                            <dt class="text-xs font-semibold uppercase text-base-content/50"
                                data-i18n="okfMetadata.usageWindowTo">Usage window to
                            </dt>
                            <dd class="mt-1">${page.usageWindow().to()}</dd>
                        </div>
                    @endif
                </dl>
            @endif

            @if(!page.sources().isEmpty())
                <div class="overflow-x-auto">
                    <table class="table table-sm">
                        <thead>
                        <tr>
                            <th data-i18n="okfMetadata.id">Id</th>
                            <th data-i18n="metadata.resource">Resource</th>
                            <th data-i18n="okfMetadata.title">Title</th>
                            <th data-i18n="okfMetadata.author">Author</th>
                            <th data-i18n="okfMetadata.usageCount">Usage count</th>
                            <th data-i18n="okfMetadata.lastModified">Last modified</th>
                        </tr>
                        </thead>
                        <tbody>
                        @for(var source : page.sources())
                            <tr>
                                <td class="font-mono">
                                    <div id="${source.id()}">${source.id()}</div>
                                </td>
                                <td>
                                    @if(source.resource() != null)
                                        <a class="link link-hover break-words font-mono" href="${source.resource()}">
                                            ${source.resource()}
                                        </a>
                                    @endif
                                </td>
                                <td>${source.title()}</td>
                                <td>
                                    @if(source.author() != null)
                                        <span class="font-mono">${source.author().identifier()}</span>
                                    @endif
                                </td>
                                <td>
                                    @if(source.usageCount() != null)
                                        ${source.usageCount()}
                                    @endif
                                </td>
                                <td>${source.lastModified()}</td>
                            </tr>
                        @endfor
                        </tbody>
                    </table>
                </div>
            @endif
        </section>
    @endif

    <!-- Trust ===================================================================================================== -->
    @if(page.generated() != null || !page.verified().isEmpty())
        <section class="mt-8 rounded-box border border-base-300 bg-base-100 p-6 shadow-sm sm:p-8"
                 data-okf-section="trust">
            <h2 class="text-xl font-semibold" data-i18n="okfMetadata.trust">Trust</h2>
            <hr class="mb-4">
            <div class="mb-5 text-sm" data-okf-field="trust-tier">
                <span class="text-xs font-semibold uppercase text-base-content/50"
                      data-i18n="okfMetadata.trustTier">Trust tier</span>
                <span class="badge badge-outline ml-2">${page.trustTierValue()}</span>
            </div>
            <div class="grid gap-6 md:grid-cols-2">
                @if(page.generated() != null)
                    <div data-okf-field="generated">
                        <div class="text-xs font-semibold uppercase text-base-content/50"
                             data-i18n="okfMetadata.generated">Generated
                        </div>
                        <dl class="mt-3 space-y-2 text-sm">
                            @if(page.generated().by() != null)
                                <div>
                                    <dt class="font-semibold" data-i18n="okfMetadata.by">By</dt>
                                    <dd class="inline-flex items-center gap-1.5">
                                        @if(page.generated().by().isHuman())
                                            @template.components.icons.human(cssClass = "size-4 shrink-0")
                                        @endif
                                        @if(page.generated().by().isAgent())
                                            @template.components.icons.agent(cssClass = "size-4 shrink-0")
                                        @endif
                                        @if(page.generated().by().isProcess())
                                            @template.components.icons.process(cssClass = "size-4 shrink-0")
                                        @endif
                                        <span class="font-mono">${page.generated().by().identifier()}</span>
                                    </dd>
                                </div>
                            @endif
                            @if(page.generated().at() != null)
                                <div>
                                    <dt class="font-semibold" data-i18n="okfMetadata.at">At</dt>
                                    <dd>${page.generated().at()}</dd>
                                </div>
                            @endif
                        </dl>
                    </div>
                @endif

                @if(!page.verified().isEmpty())
                    <div data-okf-field="verified">
                        <div class="text-xs font-semibold uppercase text-base-content/50"
                             data-i18n="okfMetadata.verified">Verified
                        </div>
                        <ul class="mt-3 space-y-3 text-sm">
                            @for(var verification : page.verified())
                                <li class="flex flex-wrap items-center gap-x-2 gap-y-1">
                                    @if(verification.by() != null)
                                        <span class="inline-flex items-center gap-1.5">
                                            @if(verification.by().isHuman())
                                                @template.components.icons.human(cssClass = "size-4 shrink-0")
                                            @endif
                                            @if(verification.by().isAgent())
                                                @template.components.icons.agent(cssClass = "size-4 shrink-0")
                                            @endif
                                            @if(verification.by().isProcess())
                                                @template.components.icons.process(cssClass = "size-4 shrink-0")
                                            @endif
                                            <span class="font-mono">${verification.by().identifier()}</span>
                                        </span>
                                    @endif
                                    @if(verification.at() != null)
                                        <span class="text-base-content/60">${verification.at()}</span>
                                    @endif
                                </li>
                            @endfor
                        </ul>
                    </div>
                @endif
            </div>
        </section>
    @endif

    <!-- Computation =============================================================================================== -->
    @if(page.attestedComputation())
        <section class="mt-8 rounded-box border border-base-300 bg-base-100 p-6 shadow-sm sm:p-8"
                 data-okf-section="computation">
            <h2 class="text-xl font-semibold" data-i18n="okfMetadata.computationContract">Computation contract</h2>
            <hr class="mb-4">

            <dl class="grid gap-4 text-sm sm:grid-cols-2">
                @if(page.runtime() != null)
                    <div data-okf-field="runtime">
                        <dt class="text-xs font-semibold uppercase text-base-content/50"
                            data-i18n="okfMetadata.runtime">Runtime
                        </dt>
                        <dd class="mt-1 font-mono">${page.runtime()}</dd>
                    </div>
                @endif
                @if(page.computation() != null)
                    <div data-okf-field="computation-file">
                        <dt class="text-xs font-semibold uppercase text-base-content/50"
                            data-i18n="okfMetadata.computationFile">Computation file
                        </dt>
                        <dd class="mt-1 font-mono">${page.computation()}</dd>
                    </div>
                @endif
                @if(page.executor() != null)
                    <div data-okf-field="executor">
                        <dt class="text-xs font-semibold uppercase text-base-content/50"
                            data-i18n="okfMetadata.executor">Executor
                        </dt>
                        <dd class="mt-1 font-mono">${page.executor().resource()}</dd>
                    </div>
                @endif
                @if(page.attester() != null)
                    <div data-okf-field="attester">
                        <dt class="text-xs font-semibold uppercase text-base-content/50"
                            data-i18n="okfMetadata.attester">Attester
                        </dt>
                        <dd class="mt-1 font-mono">${page.attester().resource()}</dd>
                    </div>
                @endif
            </dl>

            @if(!page.parameters().isEmpty())
                <div class="mt-5" data-okf-field="parameters">
                    <div class="mb-3 text-xs font-semibold uppercase text-base-content/50"
                         data-i18n="okfMetadata.parameters">Parameters
                    </div>
                    <div class="overflow-x-auto">
                        <table class="table table-sm">
                            <thead>
                            <tr>
                                <th data-i18n="okfMetadata.name">Name</th>
                                <th data-i18n="okfMetadata.type">Type</th>
                                <th data-i18n="okfMetadata.required">Required</th>
                            </tr>
                            </thead>
                            <tbody>
                            @for(var parameter : page.parameters())
                                <tr>
                                    <td class="font-mono">${parameter.name()}</td>
                                    <td class="font-mono">${parameter.type()}</td>
                                    <td>${parameter.required()}</td>
                                </tr>
                            @endfor
                            </tbody>
                        </table>
                    </div>
                </div>
            @endif

            @if(page.executor() != null && !page.executor().receipt().isEmpty())
                <div class="mt-5" data-okf-field="receipt">
                    <div class="mb-3 text-xs font-semibold uppercase text-base-content/50"
                         data-i18n="okfMetadata.receipt">Receipt
                    </div>
                    <div class="flex flex-wrap gap-2">
                        @for(var receiptField : page.executor().receipt())
                            <span class="badge badge-ghost border border-base-300 font-mono">${receiptField}</span>
                        @endfor
                    </div>
                </div>
            @endif
        </section>
    @endif

    <!-- Footer for OKF v0.1 ======================================================================================= -->
    @if(page.generatedAt() == null && page.timestamp() != null)
        <footer class="mt-6 text-sm text-base-content/60">
            <div class="flex flex-col gap-1 sm:flex-row sm:justify-end">
                <span><span class="font-semibold" data-i18n="footer.lastUpdate">Last update</span>:</span>
                <span>${page.timestamp().format(DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mm a")) + " UTC"}</span>
            </div>
        </footer>
    @endif

`
)
