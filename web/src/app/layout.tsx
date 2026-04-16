import type { Metadata } from "next";
import { Inter } from "next/font/google";
import { AuthProvider } from "@/lib/auth";
import Header from "@/components/Header";
import VerificationBanner from "@/components/VerificationBanner";
import "./globals.css";

const inter = Inter({ subsets: ["latin"] });

export const metadata: Metadata = {
  title: "Reflect",
  description: "Guided weekly review for working professionals",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <body className={inter.className}>
        <AuthProvider>
          <Header />
          <VerificationBanner />
          <main>{children}</main>
        </AuthProvider>
      </body>
    </html>
  );
}
