@param com.oakinvest.kiso.cli.model.page.ConceptPage page

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
