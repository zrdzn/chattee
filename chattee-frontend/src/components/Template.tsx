import {Header} from "../components/Header";
import {Navbar} from "../components/Navbar";

export const Template = ({ children }: any) => {
    return (
        <>
            <Header />
            <Navbar />
            <main>
                {children}
            </main>
        </>
    )
}
