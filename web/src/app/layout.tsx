import type { Metadata } from "next";
import { DM_Sans, DM_Mono, Fraunces } from "next/font/google";
import { AuthProvider } from "@/lib/auth";
import Header from "@/components/Header";
import VerificationBanner from "@/components/VerificationBanner";
import "./globals.css";

const dmSans = DM_Sans({
  subsets: ["latin"],
  weight: ["400", "500", "600"],
  variable: "--font-sans",
  display: "swap",
});

const fraunces = Fraunces({
  subsets: ["latin"],
  weight: ["400", "600"],
  variable: "--font-serif",
  display: "swap",
});

const dmMono = DM_Mono({
  subsets: ["latin"],
  weight: ["400"],
  variable: "--font-mono",
  display: "swap",
});

export const metadata: Metadata = {
  title: "Reflect",
  description: "Guided weekly review for working professionals",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en" className={`${dmSans.variable} ${fraunces.variable} ${dmMono.variable}`}>
      <body className="font-sans bg-canvas text-text-primary antialiased">
        <AuthProvider>
          <Header />
          <VerificationBanner />
          <main>{children}</main>
        </AuthProvider>
      </body>
    </html>
  );
}
