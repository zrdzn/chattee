import {Head, Html, Main, NextScript} from 'next/document'

export default function Document() {
    return (
        <Html lang="en">
            <Head>
                <link href="../styles/main.css" rel="stylesheet"/>
            </Head>
            <body className="bg-gray-100">
            <Main/>
            <NextScript/>
            </body>
        </Html>
    )
}
