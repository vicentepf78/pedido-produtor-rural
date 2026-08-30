import { defineConfig } from "@playwright/test";

export default defineConfig({
  testDir: "./e2e",
  fullyParallel: false,
  timeout: 30_000,
testIgnore: [
    ...(process.env.VISUAL_TASK03 ? [] : ["**/visual-task03.spec.ts"]),
    ...(process.env.VISUAL_TASK04 ? [] : ["**/visual-task04.spec.ts"]),
  ],
  use: {
    baseURL: "http://127.0.0.1:5173",
    viewport: { width: 390, height: 844 },
    locale: "pt-BR",
  },
  webServer: {
    command: "npm run dev -- --host 127.0.0.1 --port 5173",
    url: "http://127.0.0.1:5173",
    reuseExistingServer: !process.env.CI,
    timeout: 60_000,
  },
});
