import ComponentBus from "@/framework/ComponentBus";
import PostponedFormView from "@/components/flow/components/PostponedFormView";
import {
    PostponedFormViewKey,
    UserSelectFormViewKey,
} from "@/components/flow/types";
import UserSelectView from "@/components/flow/components/UserSelectView";

ComponentBus.getInstance().registerComponent(PostponedFormViewKey, PostponedFormView);
ComponentBus.getInstance().registerComponent(UserSelectFormViewKey, UserSelectView);
