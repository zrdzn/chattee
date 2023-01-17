import '../styles/main.css'
import { Template } from '../components/Template'
import type { AppProps } from 'next/app'

export default function App({ Component, pageProps }: AppProps) {
  return (
      <>
        <Template>
          <Component {...pageProps} />
        </Template>
      </>
  )
}
