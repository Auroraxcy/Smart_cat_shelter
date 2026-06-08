import { marked } from 'marked'
import hljs from 'highlight.js'
import 'highlight.js/styles/atom-one-dark.css'

// 配置 marked：代码块用 highlight.js 高亮
marked.setOptions({
  breaks: true,
  gfm: true,
  highlight(code, lang) {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return hljs.highlight(code, { language: lang }).value
      } catch (e) { /* ignore */ }
    }
    return hljs.highlightAuto(code).value
  }
})

// 将 AI 返回的 markdown 文本渲染为 HTML
export function renderMarkdown(text) {
  if (!text) return ''
  return marked.parse(text)
}
