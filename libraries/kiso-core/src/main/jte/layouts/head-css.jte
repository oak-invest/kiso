@import java.time.LocalDate
@import java.time.format.DateTimeFormatter

@param String assetBasePath

!{var build = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);}

<!-- CSS =========================================================================================================== -->
<link href="${assetBasePath}assets/css/daisyui.css?build=${build}" rel="stylesheet" type="text/css"/>
<link href="${assetBasePath}assets/css/themes.css?build=${build}" rel="stylesheet" type="text/css"/>
<link href="${assetBasePath}assets/css/application.css?build=${build}" rel="stylesheet" type="text/css"/>
