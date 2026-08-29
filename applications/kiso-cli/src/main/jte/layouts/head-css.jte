@import java.time.LocalDate
@import java.time.format.DateTimeFormatter

@param com.oakinvest.kiso.cli.model.util.PageMetadata pageMetadata

!{var build = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);}

<!-- CSS =========================================================================================================== -->
<link href="${pageMetadata.assetBasePath()}assets/css/daisyui.css?build=${build}" rel="stylesheet" type="text/css"/>
<link href="${pageMetadata.assetBasePath()}assets/css/themes.css?build=${build}" rel="stylesheet" type="text/css"/>
<link href="${pageMetadata.assetBasePath()}assets/css/application.css?build=${build}" rel="stylesheet" type="text/css"/>
