import Head from 'next/head'

type HeaderProperties = {
    title: string
    keywords: string
    description: string
    faviconUrl: string
}

export const Header = ({ title, keywords, description, faviconUrl }: HeaderProperties) => {
    return (
        <Head>
            <meta name="viewport" content="width=device-width, initial-scale=1" />
            <meta name="keywords" content={keywords} />
            <meta name="description" content={description} />
            <meta charSet="utf-8" />
            <link rel="icon" href={faviconUrl} />
            <title>{title}</title>
        </Head>
    )
}

Header.defaultProps = {
    title: 'Chattee',
    keywords: 'chattee, forum, engine',
    description: 'Simple Open Source forum engine made in Javalin and Next.js + Tailwindcss.',
    faviconUrl: './favicon.ico'
}