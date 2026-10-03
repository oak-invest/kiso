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
