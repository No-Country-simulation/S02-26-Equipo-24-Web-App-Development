import Link from "next/link";

export default function Login() {
    return (
        <div className="flex justify-center items-center bg-linear-to-r from-blue-100 to-blue-300 min-h-screen">
            <div className="bg-white shadow-lg p-8 rounded-lg w-full max-w-md">
                <h2 className="mb-6 font-bold text-2xl text-center">Iniciar Sesión</h2>
                <form className="space-y-4">
                    <div>
                        <label htmlFor="email" className="block font-medium text-gray-700 text-sm">Correo Electrónico</label>
                        <input type="email" id="email" className="block shadow-sm mt-1 px-3 py-2 border border-gray-300 focus:border-blue-500 rounded-md focus:outline-none focus:ring-blue-500 w-full" placeholder="Correo Electrónico" />
                    </div>
                    <div>
                        <label htmlFor="password" className="block font-medium text-gray-700 text-sm">Contraseña</label>
                        <input type="password" id="password" className="block shadow-sm mt-1 px-3 py-2 border border-gray-300 focus:border-blue-500 rounded-md focus:outline-none focus:ring-blue-500 w-full" placeholder="Contraseña" />
                    </div>
                    <div>
                        <Link href="/dashboard">
                        <button type="submit" className="bg-blue-600 hover:bg-blue-700 shadow-sm px-3 py-2 rounded-md w-full font-medium text-white text-sm">Iniciar Sesión</button>
                        </Link>
                    </div>
                </form>
            </div>
        </div>
    );   
}