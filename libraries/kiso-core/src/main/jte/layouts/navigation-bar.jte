@param com.oakinvest.kiso.core.configuration.SiteConfiguration siteConfiguration
@param com.oakinvest.kiso.core.model.html.page.PageMetadata pageMetadata

<div class="navbar bg-base-100 shadow-sm">

    <!-- Home link ================================================================================================= -->
    <div class="flex-none">
        @if(siteConfiguration.name() != null)
            <!-- If the site has a name, display it as a text link to the home page -->
            <a href="${pageMetadata.assetBasePath()}"
               aria-label="Home"
               data-i18n-aria-label="navigation.home"
               class="btn btn-ghost text-xl">
                ${siteConfiguration.name()}
            </a>
        @else
            <!-- Else use an icon button to link to the home page -->
            <a href="${pageMetadata.assetBasePath()}"
               class="btn btn-square btn-ghost"
               aria-label="Home"
               data-i18n-aria-label="navigation.home">
                @template.components.icons.home()
            </a>
        @endif
    </div>

    <!-- Navigation button ========================================================================================= -->
    <div class="flex-1">
        <label for="kiso-navigation-drawer"
               class="btn btn-square btn-ghost"
               aria-label="Open navigation"
               data-i18n-aria-label="navigation.openNavigation">
            @template.components.icons.hamburger()
        </label>
    </div>

    <!-- Search engine ============================================================================================= -->
    <div class="kiso-search flex-none"
         data-search-index-url="${pageMetadata.assetBasePath()}search-index.json"
         data-search-result-base-url="${pageMetadata.assetBasePath()}">

        <label class="input input-bordered flex items-center gap-2">
            @template.components.icons.glass(
            cssClass = "h-[1em] w-[1em] opacity-50"
            )

            <input class="kiso-search-input grow"
                   type="search"
                   placeholder="Search"
                   data-i18n-placeholder="search.placeholder"
                   autocomplete="off"
                   aria-label="Search"
                   data-i18n-aria-label="search.label"/>
        </label>

        <div class="kiso-search-results menu rounded-box mt-2 hidden w-80 max-w-[calc(100vw-1.5rem)] bg-base-100 p-2 shadow-lg"></div>

    </div>

</div>
