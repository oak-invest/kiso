@param String title
@param String description
@param gg.jte.html.HtmlContent content

<!DOCTYPE html>
<html lang="en">

<head>

    <!-- Meta information ========================================================================================== -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title}</title>
    @if(description != null)
        <meta name="description" content="${description}">
    @endif

    <!-- CSS ======================================================================================================= -->
    <link href="assets/css/daisyui@5.css" rel="stylesheet" type="text/css"/>
    <link href="assets/css/themes.css" rel="stylesheet" type="text/css"/>
    <link href="assets/css/application.css" rel="stylesheet" type="text/css"/>

    <!-- JS ======================================================================================================== -->
    <script src="assets/js/browser@4.js"></script>

</head>

<body>
<!--
Create a container that covers at least the full browser height, uses the secondary page background color from the
current DaisyUI theme, and displays text using the default readable text color for that background.
    min-h-screen - Set the minimum height of the element to the full height of the viewport
    text-base-content - Apply DaisyUI’s default text color for base surfaces
-->
<div class="min-h-screen bg-base-200 text-base-content">

    <!--
        Create a centered main container, limit its width to about 1152px, add horizontal padding so content never
        touches screen edges, and increase spacing on larger screens for better readability.
            container - Create a centered layout wrapper
            mx-auto - Center a block element horizontally
            max-w-6xl - Limit the maximum width of the container to 6xl (1152px)
    -->
    <main class="container mx-auto max-w-6xl px-4 py-8 sm:px-6 lg:py-12">

        ${content}

    </main>

</div>
</body>

</html>