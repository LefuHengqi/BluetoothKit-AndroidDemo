#!/bin/bash

# AAR 16KB 页面大小支持检测脚本
# 使用方法: ./check_aar_16kb_support.sh <aar_file_path>

# 注意: 不使用 set -e，因为我们需要处理函数的非零返回值

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 检查参数
if [ $# -eq 0 ]; then
    echo -e "${RED}错误: 请提供AAR文件路径${NC}"
    echo "使用方法: $0 <aar_file_path>"
    echo "示例: $0 body_sl/build/outputs/aar/body_sl-1.0.2.aar"
    exit 1
fi

AAR_FILE="$1"
TEMP_DIR="temp_aar_check_$$"

# 检查AAR文件是否存在
if [ ! -f "$AAR_FILE" ]; then
    echo -e "${RED}错误: AAR文件不存在: $AAR_FILE${NC}"
    exit 1
fi

echo -e "${BLUE}=== AAR 16KB 页面大小支持检测 ===${NC}"
echo -e "检测文件: ${YELLOW}$AAR_FILE${NC}"
echo

# 清理函数
cleanup() {
    if [ -d "$TEMP_DIR" ]; then
        rm -rf "$TEMP_DIR"
    fi
}

# 设置退出时清理
trap cleanup EXIT

# 解压AAR文件
echo -e "${BLUE}1. 解压AAR文件...${NC}"
unzip -q "$AAR_FILE" -d "$TEMP_DIR"

# 查找所有.so文件
echo -e "${BLUE}2. 查找.so文件...${NC}"
SO_FILES=$(find "$TEMP_DIR" -name "*.so" -type f | sort -u)

if [ -z "$SO_FILES" ]; then
    echo -e "${YELLOW}警告: 未找到.so文件${NC}"
    echo "AAR文件内容:"
    find "$TEMP_DIR" -type f
    exit 0
fi

echo "找到以下.so文件:"
echo "$SO_FILES" | while read -r file; do
    echo "  - $(basename "$file")"
done
echo

# 检查16KB对齐的函数 - 使用精确的objdump检测方法
check_16kb_alignment() {
    local so_file="$1"
    
    # 使用objdump检查LOAD段对齐
    if command -v objdump >/dev/null 2>&1; then
        echo "  使用objdump检查LOAD段对齐..."
        local alignment_info=$(objdump -p "$so_file" 2>/dev/null | grep -E "LOAD.*align" | head -1)
        if [[ -n "$alignment_info" ]]; then
            echo "  LOAD段对齐信息: $alignment_info"
            # 检查是否包含2**14 (16KB) 或更大的对齐
            if echo "$alignment_info" | grep -qE "2\*\*(1[4-9]|[2-9][0-9])"; then
                echo "  ✓ 支持16KB页面大小"
                return 0
            else
                echo "  ✗ 不支持16KB页面大小"
                return 1
            fi
        fi
    fi
    
    echo "  ⚠ 无法确定16KB页面大小支持情况"
    return 2
}

# 检查所有.so文件
echo -e "${BLUE}3. 检查16KB页面大小支持...${NC}"
echo

OVERALL_RESULT=0
CHECKED_COUNT=0
SUPPORTED_COUNT=0
UNCERTAIN_COUNT=0

# 用于存储检测结果的数组
SUPPORTED_FILES=()
UNSUPPORTED_FILES=()
UNCERTAIN_FILES=()

# 按架构分组处理.so文件
ARCH_DIRS=("armeabi-v7a" "arm64-v8a" "x86" "x86_64")
ALL_SO_FILES=()

# 收集所有.so文件
while IFS= read -r so_file; do
    if [ -f "$so_file" ]; then
        ALL_SO_FILES+=("$so_file")
    fi
done <<< "$SO_FILES"

# 按架构分组处理所有.so文件
for arch in "${ARCH_DIRS[@]}"; do
    arch_files=()
    for so_file in "${ALL_SO_FILES[@]}"; do
        if [[ "$so_file" == *"/$arch/"* ]]; then
            arch_files+=("$so_file")
        fi
    done
    
    if [ ${#arch_files[@]} -gt 0 ]; then
        echo -e "${BLUE}检查 $arch 架构:${NC}"
        for so_file in "${arch_files[@]}"; do
            echo "检查文件: $arch/$(basename "$so_file")"
            check_16kb_alignment "$so_file"
            result=$?
            
            CHECKED_COUNT=$((CHECKED_COUNT + 1))
            
            case $result in
                0)
                    SUPPORTED_COUNT=$((SUPPORTED_COUNT + 1))
                    SUPPORTED_FILES+=("$arch/$(basename "$so_file")")
                    ;;
                1)
                    OVERALL_RESULT=1
                    UNSUPPORTED_FILES+=("$arch/$(basename "$so_file")")
                    ;;
                2)
                    UNCERTAIN_COUNT=$((UNCERTAIN_COUNT + 1))
                    UNCERTAIN_FILES+=("$arch/$(basename "$so_file")")
                    ;;
            esac
            
            echo
        done
        echo
    fi
done

# 输出总结
echo -e "${BLUE}=== 检测结果总结 ===${NC}"
echo "检测的.so文件数量: $CHECKED_COUNT"
echo "支持16KB页面大小: $SUPPORTED_COUNT"
echo "不支持16KB页面大小: $((CHECKED_COUNT - SUPPORTED_COUNT - UNCERTAIN_COUNT))"
echo "不确定的文件: $UNCERTAIN_COUNT"
echo

# 显示详细的文件列表
if [ ${#SUPPORTED_FILES[@]} -gt 0 ]; then
    echo -e "${GREEN}✓ 支持16KB页面大小的文件:${NC}"
    for file in "${SUPPORTED_FILES[@]}"; do
        echo "  - $file"
    done
    echo
fi

if [ ${#UNSUPPORTED_FILES[@]} -gt 0 ]; then
    echo -e "${RED}✗ 不支持16KB页面大小的文件:${NC}"
    for file in "${UNSUPPORTED_FILES[@]}"; do
        echo "  - $file"
    done
    echo
fi

if [ ${#UNCERTAIN_FILES[@]} -gt 0 ]; then
    echo -e "${YELLOW}? 无法确定16KB页面大小支持的文件:${NC}"
    for file in "${UNCERTAIN_FILES[@]}"; do
        echo "  - $file"
    done
    echo
fi

if [ $OVERALL_RESULT -eq 0 ] && [ $UNCERTAIN_COUNT -eq 0 ]; then
    echo -e "${GREEN}✓ AAR文件支持16KB页面大小${NC}"
    exit 0
elif [ $UNCERTAIN_COUNT -gt 0 ]; then
    echo -e "${YELLOW}? AAR文件可能支持16KB页面大小，建议进一步验证${NC}"
    exit 2
else
    echo -e "${RED}✗ AAR文件不支持16KB页面大小${NC}"
    exit 1
fi