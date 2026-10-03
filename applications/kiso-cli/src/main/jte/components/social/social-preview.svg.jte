@import com.oakinvest.kiso.cli.model.image.SocialPreviewImage

@param SocialPreviewImage preview

<?xml version="1.0" encoding="UTF-8"?>
<svg xmlns="http://www.w3.org/2000/svg" width="1200" height="630" viewBox="0 0 1200 630">
    <defs>
        <linearGradient id="accentGradient" x1="0%" y1="0%" x2="100%" y2="100%">
            <stop offset="0%" stop-color="#0d9488"/>
            <stop offset="100%" stop-color="#2563eb"/>
        </linearGradient>
    </defs>

    <!-- White canvas with a quiet editorial frame. -->
    <rect width="1200" height="630" fill="#ffffff"/>
    <rect x="24" y="24" width="1152" height="582" rx="20" fill="none" stroke="#e2e8f0"/>
    <rect x="72" y="24" width="80" height="4" rx="2" fill="url(#accentGradient)"/>

    <!-- Site identity. -->
    <rect x="72" y="70" width="10" height="26" rx="5" fill="url(#accentGradient)"/>
    <text x="98" y="91" font-family="Helvetica, Arial, sans-serif"
          font-size="21" font-weight="700" fill="#334155">
        ${preview.siteName()}
    </text>

    <!-- Connected pages: a compact illustration of structured knowledge. -->
    <g transform="translate(976 58)">
        <circle cx="70" cy="55" r="64" fill="#f0fdfa"/>
        <rect x="57" y="9" width="65" height="82" rx="10" fill="#eff6ff" stroke="#bfdbfe"
              transform="rotate(12 89 50)"/>
        <rect x="26" y="20" width="65" height="82" rx="10" fill="#ffffff" stroke="#99d5ce"/>
        <rect x="39" y="36" width="25" height="6" rx="3" fill="url(#accentGradient)"/>
        <path d="M39 55 H77 M39 65 H77 M39 75 H62" fill="none" stroke="#cbd5e1"
              stroke-width="3" stroke-linecap="round"/>
        <path d="M91 78 H124 V108" fill="none" stroke="#0d9488" stroke-width="2"/>
        <circle cx="124" cy="112" r="5" fill="#0d9488"/>
        <circle cx="11" cy="16" r="4" fill="#93c5fd"/>
    </g>

    <!-- Generous text area, clear of the illustration. -->
    @if(!preview.titleLines().isEmpty())
        <text x="72" y="235" font-family="Helvetica, Arial, sans-serif"
              font-size="58" font-weight="700" letter-spacing="-1.5" fill="#0f172a">
            ${preview.titleLines().getFirst()}
        </text>
    @endif
    @if(preview.titleLines().size() > 1)
        <text x="72" y="309" font-family="Helvetica, Arial, sans-serif"
              font-size="58" font-weight="700" letter-spacing="-1.5" fill="#0f172a">
            ${preview.titleLines().get(1)}
        </text>
    @endif

    <rect x="72" y="343" width="48" height="4" rx="2" fill="url(#accentGradient)"/>

    @if(!preview.descriptionLines().isEmpty())
        <text x="72" y="391" font-family="Helvetica, Arial, sans-serif"
              font-size="27" fill="#64748b">
            ${preview.descriptionLines().getFirst()}
        </text>
    @endif
    @if(preview.descriptionLines().size() > 1)
        <text x="72" y="431" font-family="Helvetica, Arial, sans-serif"
              font-size="27" fill="#64748b">
            ${preview.descriptionLines().get(1)}
        </text>
    @endif
    @if(preview.descriptionLines().size() > 2)
        <text x="72" y="471" font-family="Helvetica, Arial, sans-serif"
              font-size="27" fill="#64748b">
            ${preview.descriptionLines().get(2)}
        </text>
    @endif

    <!-- Footer aligned with the content column. -->
    <path d="M72 533 H1128" fill="none" stroke="#e2e8f0"/>
    @if(preview.url() != null && !preview.url().isBlank())
        <text x="72" y="570" font-family="Helvetica, Arial, sans-serif"
              font-size="17" fill="#64748b">
            ${preview.url()}
        </text>
    @endif
</svg>
