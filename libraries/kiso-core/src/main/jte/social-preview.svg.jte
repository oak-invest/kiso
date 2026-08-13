@import com.oakinvest.kiso.core.model.html.image.SocialPreviewImage

@param SocialPreviewImage preview

<?xml version="1.0" encoding="UTF-8"?>
<svg xmlns="http://www.w3.org/2000/svg" width="1200" height="630" viewBox="0 0 1200 630">

    <defs>
        <linearGradient id="accentGradient" x1="0%" y1="0%" x2="100%" y2="0%">
            <stop offset="0%" style="stop-color:#06b6d4"/>
            <stop offset="100%" style="stop-color:#818cf8"/>
        </linearGradient>
        <radialGradient id="bgGlow" cx="90%" cy="10%" r="55%">
            <stop offset="0%" style="stop-color:#1d4ed8;stop-opacity:0.18"/>
            <stop offset="100%" style="stop-color:#0f172a;stop-opacity:0"/>
        </radialGradient>
    </defs>

    <!-- Background -->
    <rect width="1200" height="630" fill="#0f172a"/>
    <rect width="1200" height="630" fill="url(#bgGlow)"/>

    <!-- Top accent bar -->
    <rect width="1200" height="4" fill="url(#accentGradient)"/>

    <!-- Decorative dot grid (top right) -->
    <circle cx="890" cy="55" r="1.5" fill="#64748b" opacity="0.5"/>
    <circle cx="930" cy="55" r="1.5" fill="#64748b" opacity="0.5"/>
    <circle cx="970" cy="55" r="1.5" fill="#64748b" opacity="0.5"/>
    <circle cx="1010" cy="55" r="1.5" fill="#64748b" opacity="0.5"/>
    <circle cx="1050" cy="55" r="1.5" fill="#64748b" opacity="0.5"/>
    <circle cx="1090" cy="55" r="1.5" fill="#64748b" opacity="0.5"/>
    <circle cx="1130" cy="55" r="1.5" fill="#64748b" opacity="0.5"/>
    <circle cx="890" cy="95" r="1.5" fill="#64748b" opacity="0.5"/>
    <circle cx="930" cy="95" r="1.5" fill="#64748b" opacity="0.5"/>
    <circle cx="970" cy="95" r="1.5" fill="#64748b" opacity="0.5"/>
    <circle cx="1010" cy="95" r="1.5" fill="#64748b" opacity="0.5"/>
    <circle cx="1050" cy="95" r="1.5" fill="#64748b" opacity="0.5"/>
    <circle cx="1090" cy="95" r="1.5" fill="#64748b" opacity="0.5"/>
    <circle cx="1130" cy="95" r="1.5" fill="#64748b" opacity="0.5"/>
    <circle cx="890" cy="135" r="1.5" fill="#64748b" opacity="0.5"/>
    <circle cx="930" cy="135" r="1.5" fill="#64748b" opacity="0.5"/>
    <circle cx="970" cy="135" r="1.5" fill="#64748b" opacity="0.5"/>
    <circle cx="1010" cy="135" r="1.5" fill="#64748b" opacity="0.5"/>
    <circle cx="1050" cy="135" r="1.5" fill="#64748b" opacity="0.5"/>
    <circle cx="1090" cy="135" r="1.5" fill="#64748b" opacity="0.5"/>
    <circle cx="1130" cy="135" r="1.5" fill="#64748b" opacity="0.5"/>
    <circle cx="890" cy="175" r="1.5" fill="#64748b" opacity="0.5"/>
    <circle cx="930" cy="175" r="1.5" fill="#64748b" opacity="0.5"/>
    <circle cx="970" cy="175" r="1.5" fill="#64748b" opacity="0.5"/>
    <circle cx="1010" cy="175" r="1.5" fill="#64748b" opacity="0.5"/>
    <circle cx="1050" cy="175" r="1.5" fill="#64748b" opacity="0.5"/>
    <circle cx="1090" cy="175" r="1.5" fill="#64748b" opacity="0.5"/>
    <circle cx="1130" cy="175" r="1.5" fill="#64748b" opacity="0.5"/>
    <circle cx="890" cy="215" r="1.5" fill="#64748b" opacity="0.5"/>
    <circle cx="930" cy="215" r="1.5" fill="#64748b" opacity="0.5"/>
    <circle cx="970" cy="215" r="1.5" fill="#64748b" opacity="0.5"/>
    <circle cx="1010" cy="215" r="1.5" fill="#64748b" opacity="0.5"/>
    <circle cx="1050" cy="215" r="1.5" fill="#64748b" opacity="0.5"/>
    <circle cx="1090" cy="215" r="1.5" fill="#64748b" opacity="0.5"/>
    <circle cx="1130" cy="215" r="1.5" fill="#64748b" opacity="0.5"/>

    <!-- Decorative circles (bottom right) -->
    <circle cx="1160" cy="600" r="130" fill="none" stroke="#06b6d4" stroke-width="1" opacity="0.1"/>
    <circle cx="1160" cy="600" r="200" fill="none" stroke="#818cf8" stroke-width="1" opacity="0.07"/>
    <circle cx="1160" cy="600" r="270" fill="none" stroke="#06b6d4" stroke-width="1" opacity="0.04"/>

    <!-- Site name with gradient left accent bar -->
    <rect x="60" y="66" width="3" height="32" rx="1.5" fill="url(#accentGradient)"/>
    <text x="78" y="88"
          font-family="Helvetica, Arial, sans-serif"
          font-size="18" font-weight="600" fill="#94a3b8">
        ${preview.siteName()}
    </text>

    <!-- Title lines -->
    @if(preview.titleLines().size() > 0)
        <text x="60" y="240"
              font-family="Helvetica, Arial, sans-serif"
              font-size="58" font-weight="700" fill="#f8fafc">
            ${preview.titleLines().get(0)}
        </text>
    @endif
    @if(preview.titleLines().size() > 1)
        <text x="60" y="316"
              font-family="Helvetica, Arial, sans-serif"
              font-size="58" font-weight="700" fill="#f8fafc">
            ${preview.titleLines().get(1)}
        </text>
    @endif

    <!-- Gradient separator -->
    <rect x="60" y="354" width="72" height="2" rx="1" fill="url(#accentGradient)"/>

    <!-- Description lines -->
    @if(preview.descriptionLines().size() > 0)
        <text x="60" y="404"
              font-family="Helvetica, Arial, sans-serif"
              font-size="27" fill="#94a3b8">
            ${preview.descriptionLines().get(0)}
        </text>
    @endif
    @if(preview.descriptionLines().size() > 1)
        <text x="60" y="444"
              font-family="Helvetica, Arial, sans-serif"
              font-size="27" fill="#94a3b8">
            ${preview.descriptionLines().get(1)}
        </text>
    @endif
    @if(preview.descriptionLines().size() > 2)
        <text x="60" y="484"
              font-family="Helvetica, Arial, sans-serif"
              font-size="27" fill="#94a3b8">
            ${preview.descriptionLines().get(2)}
        </text>
    @endif

    <!-- URL -->
    @if(preview.url() != null && !preview.url().isBlank())
        <text x="60" y="600"
              font-family="Helvetica, Arial, sans-serif"
              font-size="16" fill="#475569">
            ${preview.url()}
        </text>
    @endif

</svg>
