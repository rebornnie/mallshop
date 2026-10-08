/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: '#E11D48',
        'primary-dark': '#BE123C',
        secondary: '#FB7185',
        accent: '#2563EB',
        background: '#FFF1F2',
        foreground: '#881337',
        muted: '#F0ECF2',
        border: '#FECDD3',
        destructive: '#DC2626',
      },
      fontFamily: {
        heading: ['Rubik', 'PingFang SC', 'Microsoft YaHei', 'sans-serif'],
        body: ['Nunito Sans', 'PingFang SC', 'Microsoft YaHei', 'sans-serif'],
      },
    },
  },
  plugins: [],
}
