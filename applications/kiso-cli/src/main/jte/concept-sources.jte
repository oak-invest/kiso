@import java.net.URI
@import org.apache.commons.lang3.StringUtils
@param com.oakinvest.kiso.cli.model.page.ConceptPage page

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
            <ul class="divide-y divide-base-300">
                @for(var source : page.sources())
                    <li id="${source.id()}" class="min-w-0 py-4 first:pt-0 last:pb-0 [overflow-wrap:anywhere]">
                        @if(StringUtils.isNotBlank(source.resource()))
                            !{
                                var resourceLabel = source.resource();
                                try {
                                    var resourceUri = URI.create(source.resource());
                                    if (("https".equalsIgnoreCase(resourceUri.getScheme()) || "http".equalsIgnoreCase(resourceUri.getScheme()))
                                            && resourceUri.getHost() != null) {
                                        resourceLabel = resourceUri.getHost();
                                    }
                                } catch (IllegalArgumentException exception) {
                                    // Keep free-form source descriptions unchanged.
                                }
                            }
                            <a class="link link-hover font-semibold" href="${source.resource()}">
                                @if(StringUtils.isNotBlank(source.title()))
                                    ${source.title()}
                                @else
                                    ${resourceLabel}
                                @endif
                            </a>
                            @if(StringUtils.isNotBlank(source.title()))
                                <p class="mt-1 text-sm text-base-content/60">${resourceLabel}</p>
                            @endif
                        @elseif(StringUtils.isNotBlank(source.title()))
                            <p class="font-semibold">${source.title()}</p>
                        @endif
                        @if(source.author() != null || source.usageCount() != null || StringUtils.isNotBlank(source.lastModified()))
                            <dl class="mt-2 flex flex-wrap gap-x-4 gap-y-1 text-sm text-base-content/70">
                                @if(source.author() != null)
                                    <div class="min-w-0">
                                        <dt class="inline font-medium" data-i18n="okfMetadata.author">Author</dt>:
                                        <dd class="inline">${source.author().identifier()}</dd>
                                    </div>
                                @endif
                                @if(source.usageCount() != null)
                                    <div class="min-w-0">
                                        <dt class="inline font-medium" data-i18n="okfMetadata.usageCount">Usage count</dt>:
                                        <dd class="inline">${source.usageCount()}</dd>
                                    </div>
                                @endif
                                @if(StringUtils.isNotBlank(source.lastModified()))
                                    <div class="min-w-0">
                                        <dt class="inline font-medium" data-i18n="okfMetadata.lastModified">Last modified</dt>:
                                        <dd class="inline">${source.lastModified()}</dd>
                                    </div>
                                @endif
                            </dl>
                        @endif
                        @if(StringUtils.isNotBlank(source.id()))
                            <p class="mt-2 font-mono text-xs text-base-content/50">${source.id()}</p>
                        @endif
                    </li>
                @endfor
            </ul>
        @endif
    </section>
@endif
