@import org.apache.commons.lang3.StringUtils

@param com.oakinvest.kiso.cli.model.page.LogPage page

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
            <span class="badge badge-primary badge-lg" data-i18n="pageKind.log">Log</span>
        </div>

        <!-- Title ============================================================================================= -->
        <!--
            Render a large bold title constrained to readable width, with tight line spacing and responsive font
            sizing (36px on mobile, 48px on larger screens).
                leading-tight - Tight line-height. Without it, large titles often have too much vertical spacing.
                tracking-normal - Normal letter spacing.
        -->
        @if(StringUtils.isNotBlank(page.metadata().title()))
            <div class="max-w-4xl text-4xl font-bold leading-tight tracking-normal sm:text-5xl">
                ${page.metadata().title()}
            </div>
        @endif

    </div>

</section>
