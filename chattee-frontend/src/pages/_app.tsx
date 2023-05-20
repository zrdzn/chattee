import '../styles/main.css'
import { Template } from '../components/Template'
import type { AppProps } from 'next/app'
import "@fortawesome/fontawesome-svg-core/styles.css";
import { config } from "@fortawesome/fontawesome-svg-core";
import axios from "axios"

export const Axios = axios.create({
    baseURL: `${process.env.backendUrl}/api/v1/`,
    withCredentials: true
});

config.autoAddCss = false;

export default function App({ Component, pageProps }: AppProps) {
  return (
      <>
        <Template>
          <Component {...pageProps} />
        </Template>
      </>
  )
}
