@param com.oakinvest.kiso.cli.model.page.ConceptPage page

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
