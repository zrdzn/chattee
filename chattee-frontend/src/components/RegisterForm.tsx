import { AuthFormHeader } from "./auth/AuthFormHeader";

export const RegisterForm = () => {
    return (
        <>
            <div className="flex items-center justify-center h-screen">
                <div className="min-w-fit flex-col border bg-white px-10 py-14 shadow-md rounded-[4px] ">
                    <AuthFormHeader title="Sign up"></AuthFormHeader>
                    <div className="flex flex-col text-sm rounded-md">
                        <input className="mb-5 rounded-[4px] border p-3 hover:outline-none focus:outline-none hover:border-gray-300"
                               type="email" placeholder="EMAIL"/>
                        <input className="mb-5 rounded-[4px] border p-3 hover:outline-none focus:outline-none hover:border-gray-300"
                               type="username" placeholder="USERNAME"/>
                        <input className="mb-5 border rounded-[4px] p-3 hover:outline-none focus:outline-none hover:border-gray-300"
                               type="password" placeholder="PASSWORD"/>
                        <input className="border rounded-[4px] p-3 hover:outline-none focus:outline-none hover:border-gray-300"
                               type="password" placeholder="RETYPE PASSWORD"/>
                    </div>
                    <button
                        className="mt-5 w-full border p-2 bg-gray-800 text-white rounded-[4px] hover:bg-slate-700 scale-105 duration-300"
                        type="submit">Sign up
                    </button>
                    <div className="mt-5 flex justify-between text-sm text-gray-600">
                        <a href="../account/login">Already have an account?</a>
                    </div>
                </div>
            </div>
        </>
    )
}