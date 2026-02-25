export const API_URL =
  process.env.API_URL ??
  process.env.NEXT_PUBLIC_API_URL;

if (!API_URL) {
  throw new Error("API_URL is not defined");
}