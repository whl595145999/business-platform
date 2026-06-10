#!/usr/bin/env python3
"""Convert whl-scm record types to Lombok POJOs."""
from __future__ import annotations

import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1] / "whl-scm"
PARAM_DOC = re.compile(r"@param\s+(\w+)\s+(.*)")


def getter(name: str) -> str:
    return "get" + name[0].upper() + name[1:]


def parse_param_docs(javadoc: str) -> dict[str, str]:
    return {m.group(1): m.group(2).strip() for m in PARAM_DOC.finditer(javadoc)}


def split_params(params_block: str) -> list[tuple[str, str]]:
    parts: list[str] = []
    depth = 0
    current: list[str] = []
    for ch in params_block:
        if ch == "<":
            depth += 1
        elif ch == ">":
            depth = max(0, depth - 1)
        if ch == "," and depth == 0:
            part = "".join(current).strip()
            if part:
                parts.append(part)
            current = []
            continue
        current.append(ch)
    tail = "".join(current).strip()
    if tail:
        parts.append(tail)
    out: list[tuple[str, str]] = []
    for part in parts:
        typ, name = part.rsplit(None, 1)
        out.append((typ.strip(), name.strip()))
    return out


def balanced(text: str, start: int, open_c: str, close_c: str) -> tuple[str, int]:
    depth = 0
    i = start
    while i < len(text):
        c = text[i]
        if c == open_c:
            depth += 1
        elif c == close_c:
            depth -= 1
            if depth == 0:
                return text[start + 1 : i], i + 1
        i += 1
    raise ValueError("unbalanced")


def clean_class_javadoc(javadoc: str) -> str:
    lines = []
    for line in javadoc.splitlines():
        if "@param" in line:
            continue
        lines.append(line)
    text = "\n".join(lines).rstrip()
    return text + "\n" if text else ""


def convert_one_record(text: str, start: int) -> tuple[str, int, list[str]]:
    m = re.match(r"(?P<indent> *)public record (?P<name>\w+)\s*\(", text[start:])
    if not m:
        raise ValueError("not a record")
    indent = m.group("indent")
    name = m.group("name")
    pos = start + m.end() - 1
    params_block, pos = balanced(text, pos, "(", ")")

    implements_clause = ""
    while pos < len(text) and text[pos] in " \t\r\n":
        pos += 1
    impl_match = re.match(r"implements\s+([\w.]+)", text[pos:])
    if impl_match:
        implements_clause = f" implements {impl_match.group(1)}"
        pos += impl_match.end()
    while pos < len(text) and text[pos] in " \t\r\n":
        pos += 1

    body = ""
    if pos < len(text) and text[pos] == "{":
        body, pos = balanced(text, pos, "{", "}")
        body = body.strip()

    javadoc = ""
    jdoc_start = text.rfind("/**", 0, start)
    if jdoc_start >= 0:
        jdoc_end = text.find("*/", jdoc_start)
        if jdoc_end >= 0 and jdoc_end < start:
            javadoc = text[jdoc_start : jdoc_end + 2]

    param_docs = parse_param_docs(javadoc)
    params = split_params(params_block)
    field_names = [n for _, n in params]

    nested = len(indent) > 0
    static_kw = "static " if nested else ""
    lines: list[str] = []
    if javadoc.strip():
        for jline in clean_class_javadoc(javadoc).splitlines():
            lines.append(f"{indent}{jline}" if jline else "")
    lines.append(f"{indent}@Data")
    lines.append(f"{indent}@NoArgsConstructor")
    lines.append(f"{indent}@AllArgsConstructor")
    lines.append(f"{indent}public {static_kw}class {name}{implements_clause} {{")
    lines.append("")
    for typ, field in params:
        comment = param_docs.get(field, field)
        lines.append(f"{indent}    /** {comment} */")
        lines.append(f"{indent}    private {typ} {field};")
        lines.append("")
    if body:
        lines.append(body)
        if not body.endswith("\n"):
            lines.append("")
    lines.append(f"{indent}}}")
    return "\n".join(lines), pos, field_names


def strip_prefix_javadoc(prefix: str) -> str:
    jdoc_start = prefix.rfind("/**")
    if jdoc_start < 0:
        return prefix
    jdoc_end = prefix.find("*/", jdoc_start)
    if jdoc_end < 0:
        return prefix
    return prefix[:jdoc_start]


def ensure_imports(content: str) -> str:
    imports = []
    if "import lombok.Data;" not in content:
        imports.append("import lombok.Data;")
    if "import lombok.NoArgsConstructor;" not in content:
        imports.append("import lombok.NoArgsConstructor;")
    if "import lombok.AllArgsConstructor;" not in content:
        imports.append("import lombok.AllArgsConstructor;")
    if not imports:
        return content
    pkg_end = content.find(";") + 1
    block = "\n".join(imports)
    return content[:pkg_end] + "\n\n" + block + content[pkg_end:]


def dedupe_javadocs(content: str) -> str:
    return re.sub(r"(/\*\*[\s\S]*?\*/\s*){2,}", lambda m: m.group(0).split("*/", 1)[0] + "*/\n\n", content)


def convert_file(path: Path) -> list[str]:
    content = path.read_text(encoding="utf-8")
    if "public record " not in content:
        return []
    all_fields: list[str] = []
    while "public record " in content:
        idx = content.find("public record ")
        prefix = strip_prefix_javadoc(content[:idx])
        converted, end, fields = convert_one_record(content, idx)
        content = prefix + converted + content[end:]
        all_fields.extend(fields)
    content = ensure_imports(content)
    content = dedupe_javadocs(content)
    path.write_text(content, encoding="utf-8")
    return all_fields


def patch_accessors(field_names: set[str]) -> None:
    skip = {"eventType", "version", "isBlank", "isEmpty", "trim", "stream", "map", "toList", "get", "of", "valueOf"}
    files = list(ROOT.rglob("*.java"))
    for field in sorted(field_names, key=len, reverse=True):
        if field in skip:
            continue
        pat = re.compile(rf"\.{re.escape(field)}\(\)")
        repl = f".{getter(field)}()"
        for file in files:
            text = file.read_text(encoding="utf-8")
            new = pat.sub(repl, text)
            if new != text:
                file.write_text(new, encoding="utf-8")


def main() -> int:
    all_fields: set[str] = set()
    for path in sorted(ROOT.rglob("*.java")):
        if "public record " not in path.read_text(encoding="utf-8"):
            continue
        try:
            all_fields.update(convert_file(path))
            print(f"ok {path.relative_to(ROOT.parent)}")
        except Exception as exc:  # noqa: BLE001
            print(f"FAIL {path}: {exc}", file=sys.stderr)
            return 1
    patch_accessors(all_fields)
    remaining = sum(1 for p in ROOT.rglob("*.java") if "public record " in p.read_text(encoding="utf-8"))
    print(f"fields={len(all_fields)} remaining_records={remaining}")
    return 0 if remaining == 0 else 1


if __name__ == "__main__":
    raise SystemExit(main())
