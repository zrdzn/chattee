import {Header} from "../components/Header";
import {Navbar} from "../components/Navbar";
import {Toaster} from "react-hot-toast";

export const Template = ({ children }: any) => {
    return (
        <>
            <Header />
            <Navbar />
            <main>
                <Toaster position="bottom-right"
                         reverseOrder={false}
                         toastOptions={{duration: 2000}} />
                {children}
            </main>
        </>
    )
}
