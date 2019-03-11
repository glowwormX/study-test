package leetCode;

import java.util.Arrays;

/**
 * @author 徐其伟
 * @Description:
 * @date 19-2-27 下午2:44
 */
public class ThreeSumClosest {
    public int threeSumClosest(int[] nums, int target) {
        int result = 0, abs = Integer.MAX_VALUE;
        Arrays.sort(nums);
        int len = nums.length;
        for (int i = 0; i < len - 2; i++) {
            int left = i + 1;
            int right = len - 1;
            while (left < right) {
                int sum = nums[left] + nums[right] + nums[i];
                if (sum == target) {
                    return target;
                } else if (sum < target) {
                    left++;
                } else {
                    right--;
                }
                if (Math.abs(sum - target) < abs) {
                    abs = Math.abs(sum - target);
                    result = sum;
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        new ThreeSumClosest().threeSumClosest(new int[]{1,1,1,0}, 100);
    }
}
